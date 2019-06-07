from bs4 import BeautifulSoup
import urllib.request as ur
from urllib.error import HTTPError,URLError
import re
# def Information(url):
def Information(url, filename):
    num = 0
    while True:
        num+=1
        try:
            req = ur.Request(url)
            req.add_header('User-Agent','Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36')
            rep = ur.urlopen(req)
        except HTTPError as e:
            print('\n\n****************HTTPError:    ',e.code)
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
                position = soup.find('div',class_='inner-left fl')
                r_position = position.find('h1')
                s_position = r_position.get_text('\n',strip=True)
                #print(s_position)
            except AttributeError:
                try:
                    position_campus = soup.find('div',class_='cJobDetailInforWrap')
                    r_position_campus = position_campus.find('h1')
                    s_position = r_position_campus.get_text('\n',strip=True).replace(' ','')
                except:
                    s_position = '无'
                    print('s_position AttributeError')
            #companyName公司
            try:
                company = soup.find('div',class_='inner-left fl')
                r_company = company.find('h2')
                #s_company = str(r_company.string)
                s_company = r_company.get_text()
                #print(s_company)
            except AttributeError:
                s_company = '无'
                print('s_company AttributeError')

            # salary薪资 + request简短要求 + numbers招聘人数
            try:
                tag_list = soup.find('ul',{'class':'terminal-ul clearfix'})
                list = tag_list.find_all('strong')
                p_salary=re.findall(r'<strong>([^~]+?)元/月',str(list[0]))
                if p_salary !=[]:
                    s_salary = ''.join(p_salary)+'元/月'
                else:
                    s_salary = str(list[0].get_text()).replace(' ', '').replace('\n', '').replace('\r', '')
            except AttributeError:
                s_salary = '无'
                print('s_list AttributeError')
            except IndexError:
                s_salary = '无'
                print('s_salary IndexError')
            except:
                s_salary = '无'
                print('s_salary Error')

            # request简短要求
            try:
                tag_list_edu = soup.find('ul', {'class': 'terminal-ul clearfix'})
                list_edu = tag_list_edu.find_all('strong')
                r_edu = '最低学历:' + list_edu[5].get_text().replace('\n', '').replace(' ', '')
            except AttributeError:
                r_edu = '无'
                print('r_edu AttributeError')
            except IndexError:
                r_edu = '无'
                print('r_edu IndexError')
            except:
                r_edu = '无'
                print('r_edu Error')
            try:
                tag_list_exp = soup.find('ul', {'class': 'terminal-ul clearfix'})
                list_exp = tag_list_exp.find_all('strong')
                r_exp = '工作经验:' + list_exp[4].get_text().replace('\n', '').replace(' ', '')
            except AttributeError:
                r_exp = '无'
                print('r_exp AttributeError')
            except IndexError:
                r_exp = '无'
                print('r_exp IndexError')
            except:
                r_exp = '无'
                print ('r_exp Error')

            s_request = r_edu + ',' + r_exp

            # 招聘人数
            try:
                tag_list_num = soup.find('ul', {'class': 'terminal-ul clearfix'})
                list_num = tag_list_num.find_all('strong')
                s_person_num = str(list_num[6].get_text()).replace(' ','')
            except AttributeError:
                s_person_num = '无'
                print('s_person_num AttributeError')
            except IndexError:
                s_person_num = '无'
                print('s_person_num IndexError')
            except:
                s_person_num = '无'
                print('s_person_num Error')

            # 福利
            try:
                tag_welf = soup.find('div',class_='welfare-tab-box')
                list_welf = tag_welf.find_all('span')
                s_welfare = ''
                for each in list_welf:
                    p_welf = each.get_text()
                    if s_welfare == '':
                        s_welfare = p_welf
                    else:
                        s_welfare = s_welfare + ',' + p_welf
                #print(s_welfare)
            except AttributeError:
                s_welfare = '无'
                print('s_welfare AttributeError')

            # Specific Request
            try:
                describe_tag = soup.find('div', class_='tab-inner-cont')
                describe = describe_tag.find_all('p')
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
            #print(s_duty_detail)

            #companyDes
            try:
                type = soup.find('ul',class_= 'terminal-ul clearfix terminal-company mt20')
                list = type.find_all('strong')
                s_com_type = str(list[0].string)
                s_com_scale = str(list[2].string).replace(')','')
            except AttributeError:
                s_com_type = '无'
                s_com_scale = '无'
                print('s_type or s_scale AttributeError ')
            except:
                s_com_type = '无'
                s_com_scale = '无'
                print ('s_type or s_scale Error')

            # try:
            #     com_scale = soup.find('ul',class_='terminal-ul clearfix terminal-company mt20')
            #     s_com_scale = com_scale.get_text()
            # except AttributeError:
            #     s_com_scale = '无'
            #     print ('s_com_scale AttributeError')
            # except:
            #     s_com_scale = '无'
            #     print('s_com_scale Error')

            result = s_company + symbol + s_position + symbol + s_salary + symbol + s_person_num + symbol + s_welfare + symbol + s_request + symbol + s_duty_detail + symbol + s_com_type + symbol + s_com_scale
            if result != '无>>>无>>>无>>>无>>>无>>>无,无>>>无>>>无>>>无':
                break
            if num == 100:
                break
            print('\n\n*****************************请劳驾到智联招聘 页面输入验证码********已经第{}次提醒了*******************************'.format(num))

    print (result)
    file = open(filename, 'a', encoding='utf-8')
    file.write(result + '\n')
    file.close()

if __name__ == '__main__':
    Information('http://jobs.zhaopin.com/317201937250006.htm')
    #王晨 20:24:28
# http://jobs.zhaopin.com/354307322270397.htm
# http://jobs.zhaopin.com/000537242254036.htm
# http://jobs.zhaopin.com/451373938250015.htm

