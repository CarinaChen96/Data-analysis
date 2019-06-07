from bs4 import BeautifulSoup
import urllib.request as ur
from urllib.error import HTTPError,URLError
import re
import random

# def Information(url):
def Information(url, filename):
    while True:
        try:
            req = ur.Request(url)
            req.add_header('User-Agent',
                           'Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36')
            rep = ur.urlopen(req)
        except HTTPError as e:
            print('\n\n***********HTTPError:    ',e.code)
        except URLError as e:
            print('\n\n****************URLError:  ',e.reason)
        else:
            html = rep.read().decode('utf-8')
            soup = BeautifulSoup(html, "lxml")

            symbol = '>>>'
            # 爬取positionName职位
            try:
                position = soup.find(attrs={"class": "f24 fc4b h31"})
                s_position = position.get_text()
            except AttributeError:
                s_position='无'
                print('s_position AttributeError')

            #companyName公司
            try:
                company = soup.find('span',class_='firm-name')
                s_company = str(company.find('a').string).replace(' ','').replace('\n','')
            except AttributeError:
                s_company = '无'
                print('s_company AttributeError')
            # print(s_company)
            # salary薪资
            try:
                tag_salary= soup.find('div',{'class':'d-c-left-age d-c-left-firm mt-30'})
                s_salary = str(tag_salary.find('em',{'class':'salary'}).string)
            except AttributeError:
                s_salary = '无'
                print('s_salary AttributeError')
            # print(s_salary)
            # request简短要求
            try:
                request = soup.find_all('li', class_='fl')
                s_request = ''
            except:
                s_request = '无'
                print('s_request Error')
            try:
                r_edu = '最低学历:'+ str(request[4].em.string)
            except AttributeError:
                r_edu = '无'
                print('r_edu AttributeError')
            except IndexError:
                r_edu = '无'
                print('r_edu IndexError')
            try:
                r_exp = '工作经验:'+ str(request[5].get_text()).replace('\n','').replace(' ','')
            except AttributeError:
                r_exp = '无'
                print('r_exp AttributeError')
            except IndexError:
                r_exp = '无'
                print('r_exp IndexError')
            try:
                r_age = '年龄:' + str(request[6].em.string)
            except AttributeError:
                r_age = '无'
                print('r_age AttributeError')
            except IndexError:
                r_age = '无'
                print('r_age IndexError')

            s_request = r_edu + ',' + r_exp + ',' + r_age


            # 招聘人数
            try:
                s_person_num = str(request[7].em.string)
            except AttributeError:
                s_person_num = '无'
                print('r_person_num AttributeError')
            except IndexError:
                s_person_num = '无'

            # 福利
            try:
                tag_welf = soup.find('div', class_='d-welf-items')
                list_welf = tag_welf.find_all('li')
                s_welfare = ''
                for each in list_welf:
                    p_welf = str(each.string)
                    if s_welfare == '':
                        s_welfare = p_welf
                    else:
                        s_welfare = s_welfare + ',' + p_welf
            except AttributeError:
                s_welfare = '无'
                print('s_welfare AttributeError')
            # 详细要求
            try:
                describe = soup.find_all('div', class_='deta-Corp')
                duty_desc = ''
                for each in describe:
                    get_each = each.get_text('\n', strip=True)
                    duty_desc = duty_desc + get_each
                s_duty_detail = duty_desc.replace(' ', '').replace('\n', '').replace('......', ' ')
            except AttributeError:
                s_duty_detail = '无'
                print('s_duty_detail AttributeError')
            except :
                s_duty_detail = '无'
                print('s_duty_detail Error')
            result = s_company + symbol + s_position + symbol + s_salary + symbol + s_person_num + symbol + s_welfare + symbol + s_request + symbol + s_duty_detail
            if result !='无>>>无>>>无>>>无>>>无>>>无,无,无>>>无':
                break
        print('\n\n*****************************请劳驾到赶集网页面输入验证码***************************************')

    print (result)
    file = open(filename, 'a', encoding='utf-8')
    file.write(result + '\n')
    file.close()

if __name__ == '__main__':
    Information('http://bj.ganji.com/zpruanjianhulianwang/2732556880x.htm')