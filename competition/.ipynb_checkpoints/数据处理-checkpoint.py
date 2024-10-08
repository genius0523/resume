{
 "cells": [
  {
   "cell_type": "code",
   "execution_count": 1,
   "id": "161d40b6-0819-4606-9f2b-952c016df40c",
   "metadata": {},
   "outputs": [],
   "source": [
    "import pandas as pd\n",
    "import tushare as ts\n",
    "import numpy as np"
   ]
  },
  {
   "cell_type": "code",
   "execution_count": 2,
   "id": "482041e1-0e60-44b9-85d8-f86c004b1d29",
   "metadata": {},
   "outputs": [],
   "source": [
    "pro = ts.pro_api()"
   ]
  },
  {
   "cell_type": "code",
   "execution_count": 3,
   "id": "cb2db4bf-724e-4574-8909-d503cb29cc91",
   "metadata": {},
   "outputs": [],
   "source": [
    "#从tushare读取数据\n",
    "df_0904 = pro.daily(trade_date='20230904')\n",
    "df_0905 = pro.daily(trade_date='20230905')\n",
    "df_0906 = pro.daily(trade_date='20230906')\n",
    "df_0907 = pro.daily(trade_date='20230907')\n",
    "df_0908 = pro.daily(trade_date='20230908')\n",
    "\n",
    "df = pd.concat([df_0904,df_0905,df_0906,df_0907,df_0908],axis=0)"
   ]
  },
  {
   "cell_type": "code",
   "execution_count": 4,
   "id": "e2c4b7d4-c48a-4710-a2a6-acbfc2f1599d",
   "metadata": {},
   "outputs": [],
   "source": [
    "#修改股票代码格式\n",
    "ls = []\n",
    "ts_code = []\n",
    "for i in df['ts_code'].values:\n",
    "    ls = i.split('.')\n",
    "    if ls[1] == 'SZ':\n",
    "        ls[1] = 'XSHE'\n",
    "    else:\n",
    "        ls[1] = 'XSHG'\n",
    "    ts_code.append(ls[0]+'.'+ls[1])\n",
    "\n",
    "df['ts_code'] = ts_code\n",
    "df.rename(columns={'trade_date': 'date','ts_code':'asset'}, inplace=True)"
   ]
  },
  {
   "cell_type": "code",
   "execution_count": 5,
   "id": "accc91ef-290f-4d29-b076-f20c6a36f8ac",
   "metadata": {},
   "outputs": [],
   "source": [
    "#将两个数据的date转换为同一格式\n",
    "df['date'] = df['date'].astype('int64')"
   ]
  },
  {
   "cell_type": "code",
   "execution_count": 6,
   "id": "3cbcb233-c635-4c55-b811-a4b1dbcb8eef",
   "metadata": {},
   "outputs": [],
   "source": [
    "#取出要用的列\n",
    "df = df[['asset','date','close','pre_close']]"
   ]
  },
  {
   "cell_type": "code",
   "execution_count": 7,
   "id": "932d8c95-8e4d-4b93-90c7-b16be68d2d1a",
   "metadata": {},
   "outputs": [],
   "source": [
    "#读取权重数据\n",
    "weight_data = pd.read_csv('0904data.csv')"
   ]
  },
  {
   "cell_type": "code",
   "execution_count": 8,
   "id": "569c84e6-8c1a-4857-b81c-d8d9be876f4d",
   "metadata": {},
   "outputs": [],
   "source": [
    "#合并数据，使权重与股票信息合并\n",
    "dff = df.merge(weight_data, on=['date','asset'], how='right')"
   ]
  },
  {
   "cell_type": "code",
   "execution_count": 14,
   "id": "585256c8-6fe7-43d4-b3c8-73cee846c466",
   "metadata": {},
   "outputs": [
    {
     "data": {
      "text/html": [
       "<div>\n",
       "<style scoped>\n",
       "    .dataframe tbody tr th:only-of-type {\n",
       "        vertical-align: middle;\n",
       "    }\n",
       "\n",
       "    .dataframe tbody tr th {\n",
       "        vertical-align: top;\n",
       "    }\n",
       "\n",
       "    .dataframe thead th {\n",
       "        text-align: right;\n",
       "    }\n",
       "</style>\n",
       "<table border=\"1\" class=\"dataframe\">\n",
       "  <thead>\n",
       "    <tr style=\"text-align: right;\">\n",
       "      <th></th>\n",
       "      <th>asset</th>\n",
       "      <th>date</th>\n",
       "      <th>close</th>\n",
       "      <th>pre_close</th>\n",
       "      <th>weight</th>\n",
       "    </tr>\n",
       "  </thead>\n",
       "  <tbody>\n",
       "    <tr>\n",
       "      <th>0</th>\n",
       "      <td>000001.XSHE</td>\n",
       "      <td>20230904</td>\n",
       "      <td>11.56</td>\n",
       "      <td>11.32</td>\n",
       "      <td>0.000000</td>\n",
       "    </tr>\n",
       "    <tr>\n",
       "      <th>1</th>\n",
       "      <td>000002.XSHE</td>\n",
       "      <td>20230904</td>\n",
       "      <td>14.04</td>\n",
       "      <td>14.05</td>\n",
       "      <td>0.000409</td>\n",
       "    </tr>\n",
       "    <tr>\n",
       "      <th>2</th>\n",
       "      <td>000004.XSHE</td>\n",
       "      <td>20230904</td>\n",
       "      <td>16.70</td>\n",
       "      <td>16.30</td>\n",
       "      <td>0.000449</td>\n",
       "    </tr>\n",
       "    <tr>\n",
       "      <th>3</th>\n",
       "      <td>000005.XSHE</td>\n",
       "      <td>20230904</td>\n",
       "      <td>1.47</td>\n",
       "      <td>1.43</td>\n",
       "      <td>0.000051</td>\n",
       "    </tr>\n",
       "    <tr>\n",
       "      <th>4</th>\n",
       "      <td>000006.XSHE</td>\n",
       "      <td>20230904</td>\n",
       "      <td>4.96</td>\n",
       "      <td>4.96</td>\n",
       "      <td>0.000565</td>\n",
       "    </tr>\n",
       "    <tr>\n",
       "      <th>...</th>\n",
       "      <td>...</td>\n",
       "      <td>...</td>\n",
       "      <td>...</td>\n",
       "      <td>...</td>\n",
       "      <td>...</td>\n",
       "    </tr>\n",
       "    <tr>\n",
       "      <th>25250</th>\n",
       "      <td>688799.XSHG</td>\n",
       "      <td>20230908</td>\n",
       "      <td>38.49</td>\n",
       "      <td>38.23</td>\n",
       "      <td>0.000210</td>\n",
       "    </tr>\n",
       "    <tr>\n",
       "      <th>25251</th>\n",
       "      <td>688800.XSHG</td>\n",
       "      <td>20230908</td>\n",
       "      <td>40.50</td>\n",
       "      <td>41.30</td>\n",
       "      <td>0.000170</td>\n",
       "    </tr>\n",
       "    <tr>\n",
       "      <th>25252</th>\n",
       "      <td>688819.XSHG</td>\n",
       "      <td>20230908</td>\n",
       "      <td>34.60</td>\n",
       "      <td>34.78</td>\n",
       "      <td>0.000000</td>\n",
       "    </tr>\n",
       "    <tr>\n",
       "      <th>25253</th>\n",
       "      <td>688981.XSHG</td>\n",
       "      <td>20230908</td>\n",
       "      <td>52.50</td>\n",
       "      <td>52.12</td>\n",
       "      <td>0.000107</td>\n",
       "    </tr>\n",
       "    <tr>\n",
       "      <th>25254</th>\n",
       "      <td>689009.XSHG</td>\n",
       "      <td>20230908</td>\n",
       "      <td>32.73</td>\n",
       "      <td>33.00</td>\n",
       "      <td>0.000000</td>\n",
       "    </tr>\n",
       "  </tbody>\n",
       "</table>\n",
       "<p>25255 rows × 5 columns</p>\n",
       "</div>"
      ],
      "text/plain": [
       "             asset      date  close  pre_close    weight\n",
       "0      000001.XSHE  20230904  11.56      11.32  0.000000\n",
       "1      000002.XSHE  20230904  14.04      14.05  0.000409\n",
       "2      000004.XSHE  20230904  16.70      16.30  0.000449\n",
       "3      000005.XSHE  20230904   1.47       1.43  0.000051\n",
       "4      000006.XSHE  20230904   4.96       4.96  0.000565\n",
       "...            ...       ...    ...        ...       ...\n",
       "25250  688799.XSHG  20230908  38.49      38.23  0.000210\n",
       "25251  688800.XSHG  20230908  40.50      41.30  0.000170\n",
       "25252  688819.XSHG  20230908  34.60      34.78  0.000000\n",
       "25253  688981.XSHG  20230908  52.50      52.12  0.000107\n",
       "25254  689009.XSHG  20230908  32.73      33.00  0.000000\n",
       "\n",
       "[25255 rows x 5 columns]"
      ]
     },
     "execution_count": 14,
     "metadata": {},
     "output_type": "execute_result"
    }
   ],
   "source": [
    "dff"
   ]
  },
  {
   "cell_type": "code",
   "execution_count": 9,
   "id": "81f5cf0e-3e82-4361-a619-060f8fdfce98",
   "metadata": {},
   "outputs": [],
   "source": [
    "#去除退市股票\n",
    "dff.dropna(inplace=True)"
   ]
  },
  {
   "cell_type": "code",
   "execution_count": 10,
   "id": "b26055c8-1f2a-4306-8758-3c63eade2fa0",
   "metadata": {},
   "outputs": [],
   "source": [
    "#平移权重并给第一天权重补0\n",
    "dff['pre_weight'] = dff.groupby('asset')['weight'].shift(1)\n",
    "dff.fillna(0,inplace=True)"
   ]
  },
  {
   "cell_type": "code",
   "execution_count": 11,
   "id": "d9ed1122-75f8-4980-9657-3449c4322bad",
   "metadata": {},
   "outputs": [
    {
     "name": "stdout",
     "output_type": "stream",
     "text": [
      "[1.0100239598502827, 1.0074875926769984, 1.014511237634729, 0.9977235374072241, 1.0020126801280902]\n"
     ]
    }
   ],
   "source": [
    "#计算累计收益率\n",
    "R = 1\n",
    "per = 0.0015\n",
    "all = []\n",
    "for date,data in dff.groupby('date'):\n",
    "    r = sum(((data['close']-data['pre_close'])/data['pre_close'])*data['weight'])\n",
    "    commission = sum(np.abs(data['weight']-data['pre_weight'])*per*2)\n",
    "    r -= commission\n",
    "    R = R*(1+r)\n",
    "    all.append(R)\n",
    "print(all)"
   ]
  },
  {
   "cell_type": "code",
   "execution_count": 12,
   "id": "39bf2ae0-0d5e-4900-a48b-0d3670bad6c0",
   "metadata": {},
   "outputs": [
    {
     "name": "stdout",
     "output_type": "stream",
     "text": [
      "        ts_code     name industry\n",
      "0     000001.SZ     平安银行       银行\n",
      "1     000002.SZ      万科A     全国地产\n",
      "2     000004.SZ     国华网安     软件服务\n",
      "3     000005.SZ     ST星源     环境保护\n",
      "4     000006.SZ     深振业A     区域地产\n",
      "...         ...      ...      ...\n",
      "5269  873339.BJ     恒太照明      元器件\n",
      "5270  873527.BJ      夜光明     化工原料\n",
      "5271  873576.BJ     天力复合      小金属\n",
      "5272  873593.BJ     鼎智科技     机械基件\n",
      "5273  689009.SH  九号公司-WD      摩托车\n",
      "\n",
      "[5274 rows x 3 columns]\n"
     ]
    }
   ],
   "source": [
    "#获取股票基本信息（所属行业）  （一小时仅可获取一次）\n",
    "stock_info = pro.stock_basic(list_status='L', fields='ts_code,name,industry')\n",
    "\n",
    "print(stock_info)"
   ]
  },
  {
   "cell_type": "code",
   "execution_count": 13,
   "id": "d22b112a-569f-4701-ab35-58819724422d",
   "metadata": {},
   "outputs": [],
   "source": [
    "#为获取的数据修改股票名称\n",
    "ls = []\n",
    "ts_code = []\n",
    "for i in stock_info['ts_code'].values:\n",
    "    ls = i.split('.')\n",
    "    if ls[1] == 'SZ':\n",
    "        ls[1] = 'XSHE'\n",
    "    elif ls[1] == 'SH':\n",
    "        ls[1] = 'XSHG'\n",
    "    else:\n",
    "        ls[1] = ''\n",
    "    ts_code.append(ls[0]+'.'+ls[1])\n",
    "\n",
    "stock_info['ts_code'] = ts_code\n",
    "\n",
    "stock_info.rename(columns={'ts_code':'asset'}, inplace=True)\n",
    "stock_info.drop(columns='name', inplace=True)"
   ]
  },
  {
   "cell_type": "code",
   "execution_count": 14,
   "id": "bcddb79b-8668-4b2a-b3c2-c6dbe884bc6b",
   "metadata": {},
   "outputs": [],
   "source": [
    "#合并权重数据和行业数据\n",
    "s = dff.merge(stock_info, on='asset', how='left')"
   ]
  },
  {
   "cell_type": "code",
   "execution_count": 15,
   "id": "e9765090-9969-4cf4-90b3-49676c767fcf",
   "metadata": {},
   "outputs": [
    {
     "data": {
      "text/plain": [
       "date      industry\n",
       "20230904  IT设备        0.011307\n",
       "          专用机械        0.051542\n",
       "          中成药         0.009491\n",
       "          乳制品         0.004586\n",
       "          互联网         0.021183\n",
       "                        ...   \n",
       "20230908  银行          0.005736\n",
       "          陶瓷          0.000106\n",
       "          食品          0.023670\n",
       "          饲料          0.005030\n",
       "          黄金          0.001550\n",
       "Name: weight, Length: 550, dtype: float64"
      ]
     },
     "execution_count": 15,
     "metadata": {},
     "output_type": "execute_result"
    }
   ],
   "source": [
    "s.groupby(['date','industry'])['weight'].sum()"
   ]
  },
  {
   "cell_type": "code",
   "execution_count": 16,
   "id": "61c4d9a2-9cde-45be-9f88-a2a706ad67c4",
   "metadata": {},
   "outputs": [],
   "source": [
    "data_dic = s[s['date'] == s.groupby('date').last().index[-1]].groupby('industry')['weight'].sum().sort_values().to_dict()"
   ]
  },
  {
   "cell_type": "code",
   "execution_count": 17,
   "id": "b07e17ce-c937-46fc-adff-8db66e9efff9",
   "metadata": {},
   "outputs": [],
   "source": [
    "sorted_data = dict(sorted(data_dic.items(), key=lambda item: item[1], reverse=True))"
   ]
  },
  {
   "cell_type": "code",
   "execution_count": 18,
   "id": "043157f4-2a64-4dee-8272-8bf9e6434257",
   "metadata": {},
   "outputs": [],
   "source": [
    "weight_X = list(sorted_data.keys())\n",
    "weight_y = list(sorted_data.values())"
   ]
  },
  {
   "cell_type": "code",
   "execution_count": 19,
   "id": "7e08f72d-8121-4175-b9dd-6bdf1aa30a8a",
   "metadata": {},
   "outputs": [],
   "source": [
    "pie_data = []\n",
    "for index, value in sorted_data.items():\n",
    "    pie_data.append({\"value\": value, \"name\": index})\n",
    "    if len(pie_data) == 10:\n",
    "        break"
   ]
  },
  {
   "cell_type": "code",
   "execution_count": 20,
   "id": "6131e962-5ad3-4a42-8494-21f431b3a719",
   "metadata": {},
   "outputs": [],
   "source": [
    "#需要输出的累积收益数据\n",
    "time_Y = all\n",
    "time_x = list(dff['date'].unique())"
   ]
  },
  {
   "cell_type": "code",
   "execution_count": null,
   "id": "40771bc7-60a4-4662-be1b-14685ca5cbe5",
   "metadata": {},
   "outputs": [],
   "source": []
  }
 ],
 "metadata": {
  "kernelspec": {
   "display_name": "Python 3 (ipykernel)",
   "language": "python",
   "name": "python3"
  },
  "language_info": {
   "codemirror_mode": {
    "name": "ipython",
    "version": 3
   },
   "file_extension": ".py",
   "mimetype": "text/x-python",
   "name": "python",
   "nbconvert_exporter": "python",
   "pygments_lexer": "ipython3",
   "version": "3.11.5"
  }
 },
 "nbformat": 4,
 "nbformat_minor": 5
}
