import urllib.request as ur
from bs4 import BeautifulSoup
from gjSpider import GetResult
import time
import re
Pages_list = []
#因为58同城的索引界面在不断的转变，因此写爬虫程序的时候要监测他的页面是否正确满足自己饿需求
def Spider(url):
    req = ur.Request(url)
    req.add_header('User-Agent',
                   'Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36')
    rep = ur.urlopen(req)
    html = rep.read().decode('utf-8','ignore')
    soup = BeautifulSoup(html, "html.parser")
    return soup
#设置一个方法可以读取到其中一个页面的内容，若克读取即为该页面，无法读取，则跳转到了另外的页面
def getPageUrl(url):
    list_duty_url = []
    list = 0

    page = 1
    for each in range(10000):
        time.sleep(5)
        page_url = url + 'pn' + str(page)
        soup = Spider(url)
        job_link = soup.find_all('div',class_='job_name clearfix')
        list += 1
        num = 0
        if job_link!=[]:
            print('****************************原网址*********************')
            print(page)
            page+=1

        else:
            num = 0
            while True:
                num += 1
                time.sleep(1)
                print('稍等片刻，一会回来')
                soup = Spider(url)
                # print(soup.prettify())
                job_link = soup.find_all('div', class_='job_name clearfix')
                if job_link != []:
                    print('久等了，我胡汉三又回来了，哈哈！')
                    print(page)
                    break
                print('换网址了！！！!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!！！！', num)
                print(page)


if __name__ == '__main__':
    getPageUrl('http://jn.58.com/ruanjiangong/?PGTID=0d202408-0010-9fc9-9064-2fc5cd1df94d&ClickID=1')