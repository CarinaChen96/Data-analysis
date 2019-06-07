# -*- coding: utf-8 -*
import urllib.request as ur
#import urllib.parse as up
import json
import re
import os
import time
from bs4 import BeautifulSoup
from tcSpider import tcAllDuty
#####################获得起始的地点地址，与真实爬虫项目无关，仅为获得地址的方法
def getSpace():
    # str='http://beijing.ganji.com/zpjisuanjiwangluo/'
    space_start = 'http://'     #beijing'.ganji.com/zpjisuanjiwangluo/'
    space_end = '.58.com/tech/'
    # url = ur.Request(str)
    # url.add_header('User-Agent','Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36')
    # rep = ur.urlopen(url)
    # html = rep.read().decode('utf-8')
    # soup = BeautifulSoup(html,'lxml')
    # # print(soup.prettify())
    s='北京、上海、广州、深圳、天津、杭州、南京、济南、重庆、青岛、大连、宁波、厦门、成都、武汉、哈尔滨、沈阳、西安、长春、长沙、福州、郑州、石家庄、苏州、佛山、东莞、无锡、烟台、太原、合肥'
    s1 = s.replace('、',',')
    # print(s1)
    space_name_dict={'北京':'beijing','上海':'shanghai','广州':'guangzhou','深圳':'shenzhen','天津':'tianjin',
                '杭州':'hangzhou','南京':'nanjing','济南':'jinan','重庆':'chongiqng','青岛':'qingdao',
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
######################爬虫的起始方法,将地点链接存入列表中，读取链接传入第二个方法中
def mySpaceUrl():
    enterWord = input('请输入开始指令\n\n')
    while enterWord != '开始':
        print('对不起，指令错误')
        enterWord = input('请重新输入开始指令\n\n')
    else:
        print('项目开始')
        # num = 0
        # space_address = input('请输入文件储存地址（例如：I:\\space1\\）\n\n')
        space_address = 'I:\\tcresult\\'
        print('爬虫进行中......')
        #'北京': 'http://beijing.58.com/tech', '上海': 'http://shanghai.58.com/tech', '广州': 'http://guangzhou.58.com/tech', '深圳': 'http://shenzhen.58.com/tech', '天津': 'http://tianjin.58.com/tech', '杭州': 'http://hangzhou.58.com/tech', '南京': 'http://nanjing.58.com/tech', '济南': 'http://jinan.58.com/tech', '重庆': 'http://chongiqng.58.com/tech',
        spaceUrl={'青岛': 'http://qingdao.58.com/tech', '东莞': 'http://dg.58.com/tech', '无锡': 'http://wx.58.com/tech','太原': 'http://ty.58.com/tech', '合肥': 'http://hf.58.com/tech'}
        # {'北京': 'http://beijing.58.com/tech/', '上海': 'http://shanghai.58.com/tech/',
        #  '广州': 'http://guangzhou.58.com/tech/', '深圳': 'http://shenzhen.58.com/tech/',
        #  '天津': 'http://tianjin.58.com/tech/', '杭州': 'http://hangzhou.58.com/tech/', '南京': 'http://nanjing.58.com/tech/',
        #  '济南': 'http://jinan.58.com/tech/', '重庆': 'http://chongiqng.58.com/tech/', '青岛': 'http://qingdao.58.com/tech/',
        #  '大连': 'http://dalian.58.com/tech/', '宁波': 'http://ningbo.58.com/tech/', '厦门': 'http://xiamen.58.com/tech/',
        #  '成都': 'http://chengdu.58.com/tech/', '武汉': 'http://wuhan.58.com/tech/', '哈尔滨': 'http://haerbin.58.com/tech/',
        #  '沈阳': 'http://shenyang.58.com/tech/', '西安': 'http://xian.58.com/tech/', '长春': 'http://changchun.58.com/tech/',
        #  '长沙': 'http://changsha.58.com/tech/', '福州': 'http://fuzhou.58.com/tech/',
        #  '郑州': 'http://zhengzhou.58.com/tech/', '石家庄': 'http://shijiazhuang.58.com/tech/',
        #  '苏州': 'http://suzhou.58.com/tech/', '佛山': 'http://foshan.58.com/tech/', '东莞': 'http://donguan.58.com/tech/',
        #  '无锡': 'http://wuxi.58.com/tech/', '烟台': 'http://yantai.58.com/tech/', '太原': 'http://taiyuan.58.com/tech/',
        #  '合肥': 'http://hefei.58.com/tech/', '淄博': 'http://zibo.58.com/tech/'}

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
            tcAllDuty.getduty(space_url, filename)
            print('''*********************************************************************
*********************************************************************\n\n''')


            # except OSError:
            #     print('对不起,*{}*此文件已存在'.format(space))
# print(mySpaceUrl())
# mySpaceUrl()
if __name__ == '__main__':
    getSpace()
