from bs4 import BeautifulSoup
import urllib.request as ur
import os
from gjSpider import AllDuty
import time
def city_position(url,space_file):
    positionName = []
    positionHref = []
    positon_url = {}

    req = ur.Request(url)
    req.add_header('User-Agent',
                   'Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36')
    rep = ur.urlopen(req)
    html = rep.read().decode('utf-8')
    soup = BeautifulSoup(html, "lxml")
    category = soup.find('div',id='list_seletion',class_='seltion-cont')

    url_start = url.replace('zpruanjianhulianwang/','')
    for name in category.find_all('em'):
        strName = str(name).replace('<em>','').replace('</em>','')
        positionName.append(strName)

    for link in category.find_all('a'):
        href = link.get('href')
        positionHref.append(url_start+href)

    for n in range(15):
        positon_url[positionName[n+1]] = positionHref[n+1]
    # print(positon_url)
    for key in positon_url:
        str_key = key.replace('/','')
        # 创建position文件夹
        positon_file = space_file+'\\'+str_key
        os.mkdir(positon_file)
        filename = positon_file+'\\'+str_key+'.txt'
        print("*{}*创建成功".format(filename))
        # file = open(filename,'a')
        my_object = AllDuty.getduty(positon_url[key],filename)
        # time.sleep(5)
        print ('*************该职位链接写入结束**************')
        # file.close()
    print('\n\n\n**************************此地点信息已经全部写入结束*********************')

if __name__=='--main--':
    city_position("http://bj.ganji.com/zpruanjianhulianwang/")
