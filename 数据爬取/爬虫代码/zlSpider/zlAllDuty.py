import urllib.request as ur
from bs4 import BeautifulSoup
from zlSpider import zlGetResult
import time
import re
Pages_list = []
# http://beijing.ganji.com//zpjavaruanjiankaifagongchengshi/
def Spider(url):
    # print(1)
    req = ur.Request(url)
    # print(2)
    req.add_header('User-Agent', 'Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36')
    # print(3)
    rep = ur.urlopen(req)
    # print(4)
    html = rep.read().decode('utf-8','ignore')
    soup = BeautifulSoup(html, "lxml")
    return soup

def getPageUrl(space_url):
    list_duty_url = []
    page = 1
    for each in range(50):
        page_url = space_url + 'p' + str(page)
        page += 1
        soup = Spider(page_url)
        job_link = soup.find('ul',class_='search_list')
        job_href = job_link.find_all('span',class_='post')
        num = 0
        for each_job_link in job_href:
            each_job_href = each_job_link.find('a')
            href = each_job_href.get('href')
            list_duty_url.append(href)
    return list_duty_url

def getduty(space_url,filename):
    print('*****开始读取该地点的所有职位****')
    duty_url = getPageUrl(space_url)
    print("*{}*创建成功".format(filename))
    print(filename,'****************信息开始写入该位置****************')
    num = 0
    for each_url in duty_url:
        num+=1
        zlGetResult.Information(each_url,filename)
        print('******************get one data ************第{}条'.format(str(num)))
        time.sleep(0.1)
    print('\n***************************此地点职位信息全部输入完成*******************')
    # time.sleep(2)


# if __name__ == '__main__':
#     getPageUrl('http://jobs.zhaopin.com/shanghai/bj160000//')