import urllib.request as ur
from bs4 import BeautifulSoup
from tcSpider import tcGetResult
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

def getPageUrl(space_url):
    list_duty_url = []
    # list = 0
    page = 1
    #读取的地址可浏览的网页为70页，所以设置了range（70）
    for each in range(70):
        # time.sleep(1)
        page_url = space_url+'/' + 'pn' + str(page)
        soup = Spider(page_url)
        job_link = soup.find_all('div',class_='job_name clearfix')
        num = 0
        print('****',job_link)
        #58同城共有两种网页，所以每种网页布局不同，标签不同。所以用if判断，分别使用不同对的方法去解析标签
        #将70页读取的岗位链接全部存入同一列表中
        if job_link!=[]:             #第一种网址

            for job in job_link:
                each_href = re.findall(r'href="([^~]+?)"',str(job)).pop()
                list_duty_url.append(each_href)
            page+=1
            print('**************第一种页面*************')

        else:                        #第二种网址

            print('*********************!!!第二种页面!!!****************')
            tag_link = soup.find_all('div', class_='maincon')
            print(tag_link)
            print(1)
            job2_link = re.findall(r'class="t" href="([^~]+?)"', str(tag_link[1]))
            print(2)
            if job2_link != []:
                print(3)
                list_duty_url = list_duty_url + job2_link
                print(4)
            # print('***************************', page, '********************')
            page += 1
            print(5)

    print(len(list_duty_url))
    return list_duty_url
#读取getPageUrl方法返回的列表，一次遍历，调用Information方法进行爬取数据
def getduty(space_url,filename):
    print('*****开始读取该地点的所有职位****')
    duty_url = getPageUrl(space_url)
    print("*{}*创建成功".format(filename))
    print(filename,'****************信息开始写入该位置****************')
    num = 0
    for each_url in duty_url:
        num+=1
        tcGetResult.Information(each_url,filename)
        print('******************get one data ************第{}条'.format(str(num)))
        time.sleep(0.1)
    print('\n***************************此地点职位信息全部输入完成*******************')
    # time.sleep(2)


if __name__ == '__main__':
    getPageUrl('http://fz.58.com/tech')