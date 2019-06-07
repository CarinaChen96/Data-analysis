import urllib.request as ur
from bs4 import BeautifulSoup
from gjSpider import GetResult
import time
import re
Pages_list = []
# http://beijing.ganji.com//zpjavaruanjiankaifagongchengshi/
def Spider(url):
    req = ur.Request(url)
    req.add_header('User-Agent',
                   'Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36')
    rep = ur.urlopen(req)
    html = rep.read().decode('utf-8','ignore')
    soup = BeautifulSoup(html, "lxml")
    return soup

def getPageUrl(url):
    list_duty_url = []
    # list = 0
    for page in range(100):
        # 'http://bj.ganji.com/zpyidongkaifa/'
        page_url = url + 'o' + str(page+1)
        soup = Spider(page_url)
        tag_link = soup.find_all('a',{'class':'list_title gj_tongji'})
        num = 0
        each_url=[]
        for each in tag_link:
            href = each.get('href')
            each_url.append(href)

        if each_url == []:
            break
        # time.sleep(10)
        list_duty_url = list_duty_url + each_url
    return list_duty_url

def getduty(url,filename):
    duty_url = getPageUrl(url)
    print(filename,'****************信息开始写入该位置****************')
    num = 0
    for each_url in duty_url:
        num+=1
        GetResult.Information(each_url,filename)
        print('******************get one data ************第{}条'.format(str(num)))
        time.sleep(0.1)
    print('\n***************************一职位信息全部输入完成*******************')
    # time.sleep(2)


if __name__ == '__main__':
    getPageUrl()