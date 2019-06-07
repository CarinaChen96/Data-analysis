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
    list_duty_url=[]
    for page in range(70):
        page_url = url + 'pn' + str(page+1)
        soup = Spider(page_url)
        # print(soup.prettify())
        job_link = soup.find_all('div',class_='maincon')
        # print(job_link[0])
        print(job_link[1])
        each_href = re.findall(r'class="t" href="([^~]+?)"', str(job_link[1]))
        if each_href !=[]:
            list_duty_url = list_duty_url +each_href
        print('***************************',page+1,'********************')
        print(len(each_href))
        num=0
        for a in each_href:
            num+=1
            print(num,'   ************',a)

    # print()

if __name__ == '__main__':
    getPageUrl('http://bj.58.com/tech/')