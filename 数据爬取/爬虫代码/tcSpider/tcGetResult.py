from bs4 import BeautifulSoup
import urllib.request as ur
from urllib.error import HTTPError,URLError

# def Information(url):
def Information(url, filename):
    num=0
    #建立一个while死循环，只要输出正确结果的时候才被允许跳出，或者之前设置了一个运行次数，只有当运行次数大于100的时候
    #选择跳出
    while True:
        num+=1
        try:

            req = ur.Request(url)
            req.add_header('User-Agent',
                           'Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36')
            rep = ur.urlopen(req)
        except HTTPError as e:
            print('\n\n***********HTTPError:    ',e.code)
            result = '无'
        except URLError as e:
            print('\n\n****************URLError:  ',e.reason)
            result = '无'
        except:
            print('!!!!!!!!!!!!!!!!')
            result='无'
        else:
            html = rep.read().decode('utf-8')
            soup = BeautifulSoup(html, "lxml")
            symbol = '>>>'

            # positionName职位
            try:
                position = soup.find('span',attrs={"class": "pos_name"})
                s_position = position.get_text()
                #print(s_position)
            except AttributeError:
                s_position='无'
                print('s_position AttributeError')

            #companyName公司
            try:
                company = soup.find('div',class_='baseInfo_link')
                s_company = str(company.find('a').string).replace(' ','').replace('\n','')
            except AttributeError:
                s_company = '无'
                print('s_company AttributeError')

            # salary薪资
            try:
                tag_salary= soup.find('div',{'class':'pos_base_info'})
                salary = tag_salary.find('span',class_='pos_salary')
                s_salary = salary.get_text()
            except AttributeError:
                s_salary = '无'
                print('s_salary AttributeError')

            # request简短要求
            try:
                edu = soup.find_all('span',class_ = 'item_condition')
                r_edu = '最低学历:'+ str(edu[1].string).replace(':','')
            except AttributeError:
                r_edu = '无'
                print('r_edu AttributeError')
            except IndexError:
                r_edu = '无'
                print('r_edu IndexError')
            except:
                r_edu = '无'
            try:
                exp = soup.find('span',class_='item_condition border_right_None')
                r_exp = '工作经验:'+ str(exp.get_text()).replace('\n','').replace(' ','')
            except AttributeError:
                r_exp = '无'
                print('r_exp AttributeError')
            except IndexError:
                r_exp = '无'
                print('r_exp IndexError')
            except:
                r_exp = '无'



            s_request = r_edu + ',' + r_exp


            # 招聘人数
            try:
                person_num = soup.find('span',class_='item_condition pad_left_none')
                s_person_num = str(person_num.string.replace('招','').replace(' ',''))
            except AttributeError:
                s_person_num = '无'
                print('r_person_num AttributeError')
            except IndexError:
                s_person_num = '无'
            except:
                s_person_num = '无'

            # 福利
            try:
                tag_welf = soup.find('div', class_='pos_welfare')
                list_welf = tag_welf.find_all('span')
                s_welfare = ''
                for each in list_welf:
                    #p_welf = str(each.string)
                    p_welf = each.get_text()
                    if s_welfare == '':
                        s_welfare = p_welf
                    else:
                        s_welfare = s_welfare + ',' + p_welf
            except AttributeError:
                s_welfare = '无'
                print('s_welfare AttributeError')

            # Specific Request
            try:
                describe = soup.find_all('div', class_='posDes')
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

            #companyDes
            try:
                type = soup.find('p',class_= 'comp_baseInfo_belong')
                s_com_type = str(type.string)
            except AttributeError:
                s_com_type = '无'
                print('s_type AttributeError')
            except:
                s_com_type = '无'
                print ('s_type Error')
            try:
                com_scale = soup.find('p',class_='comp_baseInfo_scale')
                s_com_scale = com_scale.get_text()
            except AttributeError:
                s_com_scale = '无'
                print ('s_com_scale AttributeError')
            except:
                s_com_scale = '无'
                print('s_com_scale Error')
            result = s_company + symbol + s_position + symbol + s_salary + symbol + s_person_num + symbol + s_welfare + symbol + s_request + symbol + s_duty_detail + symbol + s_com_type + symbol + s_com_scale
            #当出现如下结果，说明结果正确，即可以正常输出，跳出四循环，如若不然，则需要输入验证码。
            if result !='无>>>无>>>无>>>无>>>无>>>无,无>>>>>>无>>>无':
                break
            print('\n\n*****************************请劳驾到赶集网页面输入验证码*******第{}次提醒了！！！********************************'.format(num))
            #防止在失效页面中进入死循环
            if num==100:
                break
    print (result)
    file = open(filename, 'a', encoding='utf-8')
    file.write(result + '\n')
    file.close()

if __name__ == '__main__':
    #Information('http://bj.ganji.com/zpruanjianhulianwang/2732556880x.htm')
    Information('http://bj.58.com/tech/29185257351339x.shtml?PGTID=0d000000-0000-0e75-c6cd-d3cd7d82a6d6&ClickID=2&psid=159886132196564767741835652&entinfo=29185257351339_m&PGTID=0d302f82-0000-1c2b-9db0-ef2f15927621&ClickID=3')