import urllib.request as ur
from bs4 import BeautifulSoup
from gjSpider import GetResult
import time
from urllib.error import HTTPError,URLError
import re
import sys
import importlib
importlib.reload(sys)
Pages_list = []
# http://beijing.ganji.com//zpjavaruanjiankaifagongchengshi/
# 求列表中所有元素平均值
def average(list):
   getaverage = sum(list)//len(list)
   return getaverage
def Spider(url):
    req = ur.Request(url)
    req.add_header('User-Agent',
                   'Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36')
    rep = ur.urlopen(req)
    html = rep.read().decode('utf-8','ignore')
    return html
#新建方法，爬取每一页的链接，设置为一共爬取80页，然后全部放入列表中，调用average方法求平均值
def getPageUrl(url):
    city_price = []
    for page in range(80):
        page_url = url + 'o' + str(page+1)
        num = 0
        while True:
            num += 1
            try:
                req = ur.Request(page_url)
                req.add_header('User-Agent',
                               'Mozilla/5.0 (Windows NT 6.1; WOW64; rv:54.0) Gecko/20100101 Firefox/54.0')
                rep = ur.urlopen(req)
            except HTTPError as e:
                print('\n\n****************HTTPError:    ', e.code)
                # result = '无'
            except URLError as e:
                print('\n\n****************URLError:  ', e.reason)
                # result = '无'
            except:
                print('!!!!!!!!!!!!!!!!')
                # result = '无'
            else:
                html = rep.read().decode('utf-8', 'ignore')

                # houserent
                try:
                    span_tag = html
                    list_price = re.findall(r'<span class="num">([\d]+?)</span>', span_tag)
                    # print(list_price)
                    print('对应链接为    *************  ',page_url)
                    page_price = [int(i) for i in list_price]
                    city_price = city_price + page_price
                except AttributeError:
                    s_price = '无'
                    print('s_price AttributeError')
                if list_price != []:
                    break
                if num == 102:
                    break
                print(
                    '\n\n*****************************请劳驾到赶集网页面输入验证码********已经第{}次提醒了*******************************'.format(
                        num))
    # print(city_price)
    # print(len(city_price))
    price=average(city_price)
    # print(price)
    return price
#调用getPageUrl方法，求出们每个城市的平均房产
def getrent():
    city = { '沈阳': 'http://shenyang.ganji.com/fang1/h1n3o3', '西安': 'http://xian.ganji.com/fang1/h1n3o3', '长春': 'http://changchun.ganji.com/fang1/h1n3o3', '长沙': 'http://changsha.ganji.com/fang1/h1n3o3', '福州': 'http://fuzhou.ganji.com/fang1/h1n3o3', '郑州': 'http://zhengzhou.ganji.com/fang1/h1n3o3', '石家庄': 'http://shijiazhuang.ganji.com/fang1/h1n3o3', '苏州': 'http://suzhou.ganji.com/fang1/h1n3o3', '佛山': 'http://foshan.ganji.com/fang1/h1n3o3', '东莞': 'http://dg.ganji.com/fang1/h1n3o3', '无锡': 'http://wuxi.ganji.com/fang1/h1n3o3', '烟台': 'http://yantai.ganji.com/fang1/h1n3o3', '太原': 'http://taiyuan.ganji.com/fang1/h1n3o3', '合肥': 'http://hefei.ganji.com/fang1/h1n3o3'}
    # '北京': 'http://beijing.ganji.com/fang1/h1n3o3', '上海': 'http://shanghai.ganji.com/fang1/h1n3o3', '广州': 'http://guangzhou.ganji.com/fang1/h1n3o3', '深圳': 'http://shenzhen.ganji.com/fang1/h1n3o3', '天津': 'http://tianjin.ganji.com/fang1/h1n3o3', '杭州': 'http://hangzhou.ganji.com/fang1/h1n3o3', '南京': 'http://nanjing.ganji.com/fang1/h1n3o3', '济南': 'http://jinan.ganji.com/fang1/h1n3o3', '重庆': 'http://chongiqng.ganji.com/fang1/h1n3o3', '青岛': 'http://qingdao.ganji.com/fang1/h1n3o3', '大连': 'http://dalian.ganji.com/fang1/h1n3o3', '宁波': 'http://ningbo.ganji.com/fang1/h1n3o3', '厦门': 'http://xiamen.ganji.com/fang1/h1n3o3', '成都': 'http://chengdu.ganji.com/fang1/h1n3o3', '武汉': 'http://wuhan.ganji.com/fang1/h1n3o3', '哈尔滨': 'http://haerbin.ganji.com/fang1/h1n3o3',
    for space in city:
        print('成功进入***********',space)
        price = getPageUrl(city[space])
        result = space+'--'+ str(price)+'---'
        file = open('H:\\rent.txt', 'a', encoding='utf-8')
        file.write(result)
        file.write('\n')
        file.close()
        print(space,'******************end')
        print('\n***************************一地点信息全部输入完成*******************')


if __name__ == '__main__':
    getrent()






