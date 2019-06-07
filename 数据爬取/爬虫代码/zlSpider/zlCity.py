# -*- coding: utf-8 -*
import urllib.request as ur
#import urllib.parse as up
import json
import re
import os
import time
from bs4 import BeautifulSoup
from zlSpider import zlAllDuty
#####################获得起始的地点地址，与真实爬虫项目无关，仅为获得地址的方法
def getSpace():
    # str='http://beijing.ganji.com/zpjisuanjiwangluo/'
    space_start = 'http://'     #beijing'.ganji.com/zpjisuanjiwangluo/'
    space_end = '.ganji.com/fang1/h1n3'
    # url = ur.Request(str)
    # url.add_header('User-Agent','Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36')
    # rep = ur.urlopen(url)
    # html = rep.read().decode('utf-8')
    # soup = BeautifulSoup(html,'lxml')
    # # print(soup.prettify())
    s='北京、上海、广州、深圳、天津、杭州、南京、济南、重庆、青岛、大连、宁波、厦门、成都、武汉、哈尔滨、沈阳、西安、长春、长沙、福州、郑州、石家庄、苏州、佛山、东莞、无锡、烟台、太原、合肥'
    s1 = s.replace('、',',')
    # print(s1)http://cq.ganji.com/fang1/h1n3/
    space_name_dict={'北京':'beijing','上海':'shanghai','广州':'guangzhou','深圳':'shenzhen','天津':'tianjin',
                '杭州':'hangzhou','南京':'nanjing','济南':'jinan','重庆':'chongqing','青岛':'qingdao',
                '大连':'dalian','宁波':'ningbo','厦门':'xiamen','成都':'chengdu','武汉':'wuhan','哈尔滨':'haerbin',
                '沈阳':'shenyang','西安':'xian','长春':'changchun','长沙':'changsha','福州':'fuzhou','郑州':'zhengzhou',
                '石家庄':'shijiazhuang','苏州':'suzhou','佛山':'foshan','东莞':'donguan','无锡':'wuxi','烟台':'yantai',
                '太原':'taiyuan','合肥':'hefei','淄博':'zibo'}
    space_url={}
    for key in space_name_dict:
        space_str_url = space_start + space_name_dict[key] + space_end
        space_url[key]=space_str_url
    print(space_url)


# getSpace()
######################爬虫的起始方法
def mySpaceUrl():
    enterWord = input('请输入开始指令\n\n')
    while enterWord != '开始':
        print('对不起，指令错误')
        enterWord = input('请重新输入开始指令\n\n')
    else:
        print('项目开始')
        # num = 0
        # space_address = input('请输入文件储存地址（例如：I:\\space1\\）\n\n')
        space_address = 'I:\\zlresult\\'
        print('爬虫进行中......')
        #spaceUrl = {'北京': 'http://jobs.zhaopin.com/beijing/bj160000//','上海': 'http://jobs.zhaopin.com/shanghai/bj160000//', '广州': 'http://jobs.zhaopin.com/guangzhou/bj160000//', }
        #'北京': 'http://beijing.58.com/tech', '上海': 'http://shanghai.58.com/tech','广州': 'http://gz.58.com/tech', '深圳': 'http://sz.58.com/tech','天津': 'http://tj.58.com/tech','杭州': 'http://hz.58.com/tech','南京': 'http://nj.58.com/tech', '济南': 'http://jn.58.com/tech', '重庆': 'http://cq.58.com/tech'
        spaceUrl = {'青岛': 'http://jobs.zhaopin.com/qingdao/bj160000//', '大连': 'http://jobs.zhaopin.com/dalian/bj160000//', '宁波': 'http://jobs.zhaopin.com/ningbo/bj160000//','厦门': 'http://jobs.zhaopin.com/xiamen/bj160000//', '成都': 'http://jobs.zhaopin.com/chengdu/bj160000//', '武汉': 'http://jobs.zhaopin.com/wuhan/bj160000//'}
        #完成
        #问题
        #'青岛': 'http://jobs.zhaopin.com/qingdao/bj160000// ', '大连': 'http://jobs.zhaopin.com/dalian/bj160000// ', '宁波': 'http://jobs.zhaopin.com/ningbo/bj160000// ','厦门': 'http://jobs.zhaopin.com/xiamen/bj160000// ', '成都': 'http://jobs.zhaopin.com/chengdu/bj160000//', '武汉': 'http://jobs.zhaopin.com/wuhan/bj160000// '
        for space in spaceUrl:
            #try:
            # os.mkdir('I:\\space\\'+space)
            if space !='青岛':
                time.sleep(60)
            space_file = space_address+space
            os.mkdir(space_file)
            filename = space_file + '\\' + space + '.txt'
            print('\n\n*********************************************************************')
            print('恭喜你，*{}*文件创建成功'.format(space))
            space_url=spaceUrl[space]
            #print(space_url)
            zlAllDuty.getduty(space_url, filename)
            print('''*********************************************************************
*********************************************************************\n\n''')


            # except OSError:
            #     print('对不起,*{}*此文件已存在'.format(space))
# print(mySpaceUrl())
# mySpaceUrl()
# if __name__ == '__main__':
#     getSpace()
getSpace()