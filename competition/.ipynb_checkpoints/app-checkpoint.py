from flask import Flask, request, render_template_string, render_template
import json
import os
import pandas as pd
import tushare as ts
import numpy as np

app = Flask(__name__)

@app.route('/')
def index():
    
    # 获取当前脚本文件的目录
    script_dir = os.path.dirname(__file__)
    
    # 构建相对路径到index.html文件
    relative_path = 'index.html'
    index_html_path = os.path.join(script_dir, relative_path)
    
    # 打开并读取index.html文件
    with open(index_html_path, 'r', encoding='utf-8') as file:
        html_content = file.read()
    
    return render_template_string(html_content)

@app.route('/upload', methods=['POST'])
def upload_file():
    file = request.files['file']
    if file:

        #读取权重数据
        weight_data = pd.read_csv(file)

        ts.set_token('8346e81f2e0e0fef373813b4b699521a5c40d63eddeefdad1df622a9')
        
        pro = ts.pro_api()

        #从tushare读取数据

        df_0828 = pro.daily(trade_date='20230828')
        df_0829 = pro.daily(trade_date='20230829')
        df_0830 = pro.daily(trade_date='20230830')
        df_0831 = pro.daily(trade_date='20230831')
        df_0901 = pro.daily(trade_date='20230901')

        df_0904 = pro.daily(trade_date='20230904')
        df_0905 = pro.daily(trade_date='20230905')
        df_0906 = pro.daily(trade_date='20230906')
        df_0907 = pro.daily(trade_date='20230907')
        df_0908 = pro.daily(trade_date='20230908')

        df = pd.concat([df_0828,df_0829,df_0830,df_0831,df_0901,df_0904,df_0905,df_0906,df_0907,df_0908],axis=0)

        #读取停牌复牌数据
        suspend_0828 = pro.query('suspend', ts_code='', suspend_date='20230828', resume_date='', fields='')
        resume_0828 = pro.query('suspend', ts_code='', suspend_date='', resume_date='20230828', fields='')
        
        suspend_0829 = pro.query('suspend', ts_code='', suspend_date='20230829', resume_date='', fields='')
        resume_0829 = pro.query('suspend', ts_code='', suspend_date='', resume_date='20230829', fields='')
        
        suspend_0830 = pro.query('suspend', ts_code='', suspend_date='20230830', resume_date='', fields='')
        resume_0830 = pro.query('suspend', ts_code='', suspend_date='', resume_date='20230830', fields='')
        
        suspend_0831 = pro.query('suspend', ts_code='', suspend_date='20230831', resume_date='', fields='')
        resume_0831 = pro.query('suspend', ts_code='', suspend_date='', resume_date='20230831', fields='')
        
        suspend_0901 = pro.query('suspend', ts_code='', suspend_date='20230901', resume_date='', fields='')
        resume_0901 = pro.query('suspend', ts_code='', suspend_date='', resume_date='20230901', fields='')
        
        suspend_0904 = pro.query('suspend', ts_code='', suspend_date='20230904', resume_date='', fields='')
        resume_0904 = pro.query('suspend', ts_code='', suspend_date='', resume_date='20230904', fields='')
        
        suspend_0905 = pro.query('suspend', ts_code='', suspend_date='20230905', resume_date='', fields='')
        resume_0905 = pro.query('suspend', ts_code='', suspend_date='', resume_date='20230905', fields='')
        
        suspend_0906 = pro.query('suspend', ts_code='', suspend_date='20230906', resume_date='', fields='')
        resume_0906 = pro.query('suspend', ts_code='', suspend_date='', resume_date='20230906', fields='')
        
        suspend_0907 = pro.query('suspend', ts_code='', suspend_date='20230907', resume_date='', fields='')
        resume_0907 = pro.query('suspend', ts_code='', suspend_date='', resume_date='20230907', fields='')
        
        suspend_0908 = pro.query('suspend', ts_code='', suspend_date='20230908', resume_date='', fields='')
        resume_0908 = pro.query('suspend', ts_code='', suspend_date='', resume_date='20230908', fields='')
        
        suspend = pd.concat([suspend_0828,suspend_0829,suspend_0830,suspend_0831,suspend_0901,suspend_0904,suspend_0905,suspend_0906,suspend_0907,suspend_0908])[['ts_code','suspend_date']]
        resume = pd.concat([resume_0828,resume_0829,resume_0830,resume_0831,resume_0901,resume_0904,resume_0905,resume_0906,resume_0907,resume_0908])[['ts_code','resume_date']]

        #对停牌复牌进行标签
        suspend['suspend_type'] = 'S'
        resume['suspend_type'] = 'R'

        suspend.rename(columns={'suspend_date': 'date','ts_code':'asset'}, inplace=True)
        resume.rename(columns={'resume_date': 'date','ts_code':'asset'}, inplace=True)

        suspend_df = pd.concat([suspend,resume])

        #修改股票代码格式
        ls = []
        ts_code = []
        for i in suspend_df['asset'].values:
            ls = i.split('.')
            if ls[1] == 'SZ':
                ls[1] = 'XSHE'
            elif ls[1] == 'SH':
                ls[1] = 'XSHG'
            else:
                ls[1] = ''
            ts_code.append(ls[0]+'.'+ls[1])

        suspend_df['asset'] = ts_code
        suspend_df['date'] = suspend_df['date'].astype('int64')

        
        #修改股票代码格式
        ls = []
        ts_code = []
        for i in df['ts_code'].values:
            ls = i.split('.')
            if ls[1] == 'SZ':
                ls[1] = 'XSHE'
            else:
                ls[1] = 'XSHG'
            ts_code.append(ls[0]+'.'+ls[1])

        df['ts_code'] = ts_code
        df.rename(columns={'trade_date': 'date','ts_code':'asset'}, inplace=True)

        #将两个数据的date转换为同一格式
        df['date'] = df['date'].astype('int64')
        
        #取出要用的列
        df = df[['asset','date','close','pre_close']]


        #合并数据，使权重与股票信息合并
        dff = df.merge(weight_data, on=['date','asset'], how='right')
        dff = dff.merge(suspend_df, on=['date','asset'], how='left')

        #去除退市股票
        dff.drop(dff[dff['weight'] == 0].index, inplace=True)


        # 股票停牌、退市处理
        groups = dff.groupby('asset')
        groups_ready = []
        for stock_key, stock_data in groups:
            # 初始时间段，股票停牌处理
            stock_data = stock_data.reset_index(drop=True)
            first_valid_index = stock_data['close'].first_valid_index()
            stock_data.drop(stock_data[stock_data.index < first_valid_index].index, inplace=True)
            stock_data = stock_data.reset_index(drop=True)

            # 股票退市处理
            close_last_valid_index = stock_data['close'].last_valid_index()
            suspend_type_last_valid_index = stock_data['suspend_type'].last_valid_index()
            if suspend_type_last_valid_index:
                if suspend_type_last_valid_index > close_last_valid_index:
                    last_index = suspend_type_last_valid_index + 1
                    stock_data.drop(stock_data[stock_data.index > last_index].index, inplace=True)
                    stock_data.loc[last_index, 'pre_close'] = 1
                    stock_data.loc[last_index, 'close'] = 0

            stock_data.drop('suspend_type', axis=1, inplace=True)
            groups_ready.append(stock_data)

        # 合并时处理所有待处理的停牌股票
        right_data = pd.concat(groups_ready).sort_values(by='date', ascending=True).fillna(1).reset_index(drop=True)


        # 再次权重归一化
        to_normalize_groups = right_data.groupby('date')
        normalized_groups = []
        for name, group in to_normalize_groups:
            group.loc[group['weight'] < 0, 'weight'] = 0
            group['weight'] = group['weight'] / group['weight'].sum()
            # 调整，以确保权重和在指定精度范围内
            while True:
                sum_normalized = group['weight'].sum()
                if sum_normalized < 0.99999:
                    adjustment_factor = 0.99999 / sum_normalized
                    group['weight'] *= adjustment_factor
                elif sum_normalized > 1.00001:
                    adjustment_factor = 1.00001 / sum_normalized
                    group['weight'] *= adjustment_factor
                else:
                    break

            normalized_groups.append(group)
        normalized_index = pd.concat(normalized_groups)

        # 创造pre_weight列
        normalized_index['pre_weight'] = normalized_index.groupby('asset')['weight'].shift(1)
        normalized_index.fillna(0,inplace=True)
        
        #计算累计收益率
        R = 1
        per = 0.0015
        all = []
        for date,data in normalized_index.groupby('date'):
            r = sum(((data['close']-data['pre_close'])/data['pre_close'])*data['pre_weight'])
            commission = sum(np.abs(data['weight']-data['pre_weight'])*per*2)
            r -= commission
            R = R*(1+r)
            all.append(R-1)

        #获取股票基本信息（所属行业）
        # 获取当前文件所在目录的绝对路径
        current_dir = os.path.dirname(os.path.abspath(__file__))

        # 构建相对路径到模板文件
        template_file_path = os.path.join(current_dir, 'info.csv')
        
        stock_info = pd.read_csv(template_file_path)

        #为获取的数据修改股票名称
        ls = []
        ts_code = []
        for i in stock_info['ts_code'].values:
            ls = i.split('.')
            if ls[1] == 'SZ':
                ls[1] = 'XSHE'
            elif ls[1] == 'SH':
                ls[1] = 'XSHG'
            else:
                ls[1] = ''
            ts_code.append(ls[0]+'.'+ls[1])

        stock_info['ts_code'] = ts_code

        stock_info.rename(columns={'ts_code':'asset'}, inplace=True)
        stock_info.drop(columns='name', inplace=True)

        #合并权重数据和行业数据
        s = dff.merge(stock_info, on='asset', how='left')

        data_dic = s[s['date'] == s.groupby('date').last().index[-1]].groupby('industry')['weight'].sum().sort_values().to_dict()

        sorted_data = dict(sorted(data_dic.items(), key=lambda item: item[1], reverse=True))

        weight_X = list(sorted_data.keys())
        weight_y = list(sorted_data.values())

        pie_data = []

        sum_weight = 0
        
        for index, value in sorted_data.items():
            
            sum_weight += value
            
            pie_data.append({"value": value, "name": index})
            if len(pie_data) == 10:
                break

        #需要输出的累积收益数据
        time_y = all
    
        time_X = list(dff['date'].unique())

        sum_weight = str(round(sum_weight*100,2))+'%'       
        sum_weight = json.dumps(sum_weight)
        pie_data = json.dumps(pie_data)
        weight_X = json.dumps(weight_X)
        weight_y = json.dumps(weight_y)

        max_abs_value = 0
        for number in all:
            # 计算当前元素的绝对值
            abs_value = abs(number)
            
            # 检查是否为最大的绝对值
            if abs_value > max_abs_value:
                max_abs_value = abs_value
                
        max_num=[]
        min_num=[]
        max_num.append(max_abs_value)
        min_num.append(-1*max_abs_value)
        
        max_num = json.dumps(max_num)
        min_num = json.dumps(min_num)

        
        time_y = json.dumps(time_y)
        time_X = [str(x) for x in time_X]
        time_X = json.dumps(time_X)
        
        
        html_content_weight = render_template("weight.html", sum_weight=sum_weight, pie_data=pie_data, weight_X=weight_X, weight_y=weight_y)
        html_content_R = render_template("R.html", time_X=time_X, time_y=time_y, max_num=max_num, min_num=min_num)

        

        
         # 保存渲染后的HTML到临时文件
         
        with open(os.path.join(current_dir, 'templates/temp_weight.html'), "w", encoding='utf-8') as temp_file:
            temp_file.write(html_content_weight)

        with open(os.path.join(current_dir, 'templates/temp_R.html'), "w", encoding='utf-8') as temp_file:
            temp_file.write(html_content_R)


        return "File processed successfully"

@app.route('/open_html1')
def open_html1():
    return render_template('temp_weight.html')  # 'temp_weight.html' 是放在 'templates' 文件夹下的 HTML 文件名

@app.route('/open_html2')
def open_html2():
    return render_template('temp_R.html')  # 'temp_R.html' 是放在 'templates' 文件夹下的 HTML 文件名

if __name__ == '__main__':
    
    app.run(debug=True)
