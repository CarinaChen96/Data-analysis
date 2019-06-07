# -*- coding: utf-8 -*
import urllib.request as ur
#import urllib.parse as up
import json
import re
import os
import time
from bs4 import BeautifulSoup
from gjSpider import Position
#####################获得起始的地点地址，与真实爬虫项目无关，仅为获得地址的方法
def getSpace():
    str='http://beijing.ganji.com/zpjisuanjiwangluo/'
    space_start = 'http://'     #beijing'.ganji.com/zpjisuanjiwangluo/'
    space_end = '.ganji.com/zpruanjianhulianwang/'
    url = ur.Request(str)
    url.add_header('User-Agent','Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36')
    rep = ur.urlopen(url)
    html = rep.read().decode('utf-8')
    soup = BeautifulSoup(html,'lxml')
    # print(soup.prettify())
    s='北京、上海、广州、深圳、天津、杭州、南京、济南、重庆、青岛、大连、宁波、厦门、成都、武汉、哈尔滨、沈阳、西安、长春、长沙、福州、郑州、石家庄、苏州、佛山、东莞、无锡、烟台、太原、合肥'
    s1 = s.replace('、',',')
    # print(s1)
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
        space_address = 'I:\\result\\'
        print('爬虫进行中......')
        # http://jn.ganji.com/zpruanjianhulianwang/
        # spaceUrl = {'北京': 'http://beijing.ganji.com/zpjisuanjiwangluo/', '上海': 'http://shanghai.ganji.com/zpjisuanjiwangluo/', '广州': 'http://guangzhou.ganji.com/zpjisuanjiwangluo/', '深圳': 'http://shenzhen.ganji.com/zpjisuanjiwangluo/', '天津': 'http://tianjin.ganji.com/zpjisuanjiwangluo/', '杭州': 'http://hangzhou.ganji.com/zpjisuanjiwangluo/', '南京': 'http://nanjing.ganji.com/zpjisuanjiwangluo/', '济南': 'http://jinan.ganji.com/zpjisuanjiwangluo/', '重庆': 'http://chongiqng.ganji.com/zpjisuanjiwangluo/', '青岛': 'http://qingdao.ganji.com/zpjisuanjiwangluo/', '大连': 'http://dalian.ganji.com/zpjisuanjiwangluo/', '宁波': 'http://ningbo.ganji.com/zpjisuanjiwangluo/', '厦门': 'http://xiamen.ganji.com/zpjisuanjiwangluo/', '成都': 'http://chengdu.ganji.com/zpjisuanjiwangluo/', '武汉': 'http://wuhan.ganji.com/zpjisuanjiwangluo/', '哈尔滨': 'http://haerbin.ganji.com/zpjisuanjiwangluo/', '沈阳': 'http://shenyang.ganji.com/zpjisuanjiwangluo/', '西安': 'http://xian.ganji.com/zpjisuanjiwangluo/', '长春': 'http://changchun.ganji.com/zpjisuanjiwangluo/', '长沙': 'http://changsha.ganji.com/zpjisuanjiwangluo/', '福州': 'http://fuzhou.ganji.com/zpjisuanjiwangluo/', '郑州': 'http://zhengzhou.ganji.com/zpjisuanjiwangluo/', '石家庄': 'http://shijiazhuang.ganji.com/zpjisuanjiwangluo/', '苏州': 'http://suzhou.ganji.com/zpjisuanjiwangluo/', '佛山': 'http://foshan.ganji.com/zpjisuanjiwangluo/', '东莞': 'http://donguan.ganji.com/zpjisuanjiwangluo/', '无锡': 'http://wuxi.ganji.com/zpjisuanjiwangluo/', '烟台': 'http://yantai.ganji.com/zpjisuanjiwangluo/', '太原': 'http://taiyuan.ganji.com/zpjisuanjiwangluo/', '合肥': 'http://hefei.ganji.com/zpjisuanjiwangluo/', '淄博': 'http://zibo.ganji.com/zpjisuanjiwangluo/'}
        spaceUrl = {'北京': 'http://beijing.ganji.com/zpruanjianhulianwang/', '上海': 'http://shanghai.ganji.com/zpruanjianhulianwang/', '广州': 'http://guangzhou.ganji.com/zpruanjianhulianwang/', '深圳': 'http://shenzhen.ganji.com/zpruanjianhulianwang/', '天津': 'http://tianjin.ganji.com/zpruanjianhulianwang/', '杭州': 'http://hangzhou.ganji.com/zpruanjianhulianwang/', '南京': 'http://nanjing.ganji.com/zpruanjianhulianwang/', '济南': 'http://jinan.ganji.com/zpruanjianhulianwang/', '重庆': 'http://chongqing.ganji.com/zpruanjianhulianwang/', '青岛': 'http://qingdao.ganji.com/zpruanjianhulianwang/', '大连': 'http://dalian.ganji.com/zpruanjianhulianwang/', '宁波': 'http://ningbo.ganji.com/zpruanjianhulianwang/', '厦门': 'http://xiamen.ganji.com/zpruanjianhulianwang/', '成都': 'http://chengdu.ganji.com/zpruanjianhulianwang/', '武汉': 'http://wuhan.ganji.com/zpruanjianhulianwang/', '哈尔滨': 'http://haerbin.ganji.com/zpruanjianhulianwang/', '沈阳': 'http://shenyang.ganji.com/zpruanjianhulianwang/', '西安': 'http://xian.ganji.com/zpruanjianhulianwang/', '长春': 'http://changchun.ganji.com/zpruanjianhulianwang/', '长沙': 'http://changsha.ganji.com/zpruanjianhulianwang/', '福州': 'http://fuzhou.ganji.com/zpruanjianhulianwang/', '郑州': 'http://zhengzhou.ganji.com/zpruanjianhulianwang/', '石家庄': 'http://shijiazhuang.ganji.com/zpruanjianhulianwang/', '苏州': 'http://suzhou.ganji.com/zpruanjianhulianwang/', '佛山': 'http://foshan.ganji.com/zpruanjianhulianwang/', '东莞': 'http://donguan.ganji.com/zpruanjianhulianwang/', '无锡': 'http://wuxi.ganji.com/zpruanjianhulianwang/', '烟台': 'http://yantai.ganji.com/zpruanjianhulianwang/', '太原': 'http://taiyuan.ganji.com/zpruanjianhulianwang/', '合肥': 'http://hefei.ganji.com/zpruanjianhulianwang/', '淄博': 'http://zibo.ganji.com/zpruanjianhulianwang/'}

        # {'石家庄': 'http://shijiazhuang.ganji.com/zpruanjianhulianwang/', '苏州': 'http://suzhou.ganji.com/zpruanjianhulianwang/', '佛山': 'http://foshan.ganji.com/zpruanjianhulianwang/', '东莞': 'http://donguan.ganji.com/zpruanjianhulianwang/', '无锡': 'http://wuxi.ganji.com/zpruanjianhulianwang/', '烟台': 'http://yantai.ganji.com/zpruanjianhulianwang/', '太原': 'http://taiyuan.ganji.com/zpruanjianhulianwang/', '合肥': 'http://hefei.ganji.com/zpruanjianhulianwang/', '淄博': 'http://zibo.ganji.com/zpruanjianhulianwang/','北京': 'http://beijing.ganji.com/zpruanjianhulianwang/'}
        # '上海': 'http://shanghai.ganji.com/zpruanjianhulianwang/''郑州': 'http://zhengzhou.ganji.com/zpruanjianhulianwang/',
        #'重庆': 'http://chongiqng.ganji.com/zpruanjianhulianwang/', '青岛': 'http://qingdao.ganji.com/zpruanjianhulianwang/', '大连': 'http://dalian.ganji.com/zpruanjianhulianwang/', '宁波': 'http://ningbo.ganji.com/zpruanjianhulianwang/', '厦门': 'http://xiamen.ganji.com/zpruanjianhulianwang/', '成都': 'http://chengdu.ganji.com/zpruanjianhulianwang/', '武汉': 'http://wuhan.ganji.com/zpruanjianhulianwang/', '哈尔滨': 'http://haerbin.ganji.com/zpruanjianhulianwang/', '沈阳': 'http://shenyang.ganji.com/zpruanjianhulianwang/', '西安': 'http://xian.ganji.com/zpruanjianhulianwang/', '长春': 'http://changchun.ganji.com/zpruanjianhulianwang/', '长沙': 'http://changsha.ganji.com/zpruanjianhulianwang/', '福州': 'http://fuzhou.ganji.com/zpruanjianhulianwang/',
        for space in spaceUrl:
            #try:
            # os.mkdir('I:\\space\\'+space)
            space_file = space_address+space
            os.mkdir(space_file)
            print('\n\n*********************************************************************')
            print('恭喜你，*{}*文件创建成功'.format(space))
            space_url=spaceUrl[space]
            Position.city_position(space_url, space_file)
            print('''*********************************************************************
*********************************************************************\n\n''')


# mySpaceUrl()
getSpace()