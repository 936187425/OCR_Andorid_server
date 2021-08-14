from flask import Flask,app,request, make_response
from wsgiref.simple_server import make_server
import  os
import  random,string
import numpy as np
from PIL import Image
from ocr import ocr
#from app import request,json,url_for

app_1=Flask(__name__)
basedir = os.path.abspath(os.path.dirname(__file__)) #定义一个根目录存放image

#定义一个识别模型入口函数
#result:narray;image_framed:file(通过检测文本区域后的图片)
def single_pic_proc(image_file):
    image = np.array(Image.open(image_file).convert('RGB'))
    result, image_framed = ocr(image)  #ocr:detection,recognization
    return result,image_framed


@app_1.route("/")
def hello_world():
    print("1")
    return "Helloword"



@app_1.route("/ocr",methods=['POST'])
def out():
    #生成随机字符串,防止图片名字重复
    ran_str=''.join(random.sample(string.ascii_letters + string.digits, 16))

    #获取来自客户端的图片文件 name=upload
    img=request.files.get('picture') #表单的name
    path=basedir+"/test_images/"
    imgName=ran_str+img.filename#为了图片名称的一致性
    #存放结果放在static文件夹下
    file_path=path+imgName+".jpg"
    img.save(file_path)
    #对模型分析
    #result :nadarry(回归框的位置) ,image_framed:image
    result,image_framed=single_pic_proc(file_path)
    #存储模型output进行回归框框出的图片
    output_file=basedir+"/test_result/test_images/"+imgName+".jpg"
    Image.fromarray(image_framed).save(output_file)
    url="/test_images/img/"+imgName
    #返回结果
    print(output_file)
    outString=""
    for key in result:
        outString+=result[key][1]
    return output_file+"$"+outString#以美元符号为分割符,output_file为图片的路径,outString为图片的文本



#给前端发送image_framed
@app_1.route("/getBBImage",methods=['POST'])
def outBBImage():
    input=request.form.get("bbImagePath")
    image_data=open(input,"rb").read()
    response=make_response(image_data)
    response.headers['Content-Type'] = 'image/jpg'
    return response




if __name__=="__main__":
    #在不同局域网里面需要改成不同的uri ipconfig
    server = make_server('192.168.43.51', 5000, app_1)
    server.serve_forever()
    app_1.run()