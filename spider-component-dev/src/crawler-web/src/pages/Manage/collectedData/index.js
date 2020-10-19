/**
 * 管理员-项目列表页
 * 创建： 2020-09-17
 */
import './index.scss';
import React, { Component } from 'react';
import detailsMore from './detailsMore';
import { Row, Col, Form, Input, Checkbox, Table, Divider } from 'antd';
import Box from '../../../components/Box';
import { apiGetProjectList, apiGetAllUser,apoGetProjectName} from './service';
import { Select } from 'antd';
import { DatePicker, Space } from 'antd';
import { Pagination } from 'antd';

const { Option } = Select;

const layout = {
    labelCol: { span: 6 },
    wrapperCol: { span: 18 },
};

class collectedData extends Component {
    formRef = React.createRef();
    constructor(props) {
        super(props);
        this.state = {
            projectName:[],   //项目名称下拉框
            userlist:[], //用户名称下拉框
            proColumns: [
                {
                    title: '网站名称',
                    dataIndex: 'webName',
                    key: 'webName',
                    width: 100
                },
                {
                    title: '板块名称',
                    dataIndex: 'sectionTitle',
                    key: 'sectionTitle',
                    width: 100
                },
                {
                    title: '文章id',
                    dataIndex: 'articleID',
                    key: 'articleID',
                    width: 100
                },
                {
                    title: '文章标题',
                    dataIndex: 'articleTitle',
                    key: 'articleTitle',
                    width: 100
                },
                {
                    title: '采集时间',
                    dataIndex: 'date',
                    key: 'date',
                    width: 100
                },
                {
                    title: '操作',
                    dataIndex: 'other',
                    key: 'other',
                    width: 150,
                    
                    render: (text, record) => (
                        <div className="project-list-btn">
                           <button>查看详情</button>
                           {//添加查看详情的链接______TEXT
                                <detailsMore 
                                // 获取详情信息
                                    getArtDetil={this.getArtDetil}
                                    handleClose={()=>{this.setState({isShowPop: false})}}
                                />
                            }
                        </div>
                    )
                }
            ], // 项目表格表头配置项
            proData: [], // 项目列表
            date: [], // 时间
            isShowPop: false, // 是否显示详情页
            


        }
    }

    componentDidMount(){
        // this.getProjectList(); // 获取项目列表
        this.getAllUser(); // 获取所有用户信息
        this.getProjectName();//获取项目名称
        this.handleSearch();

    }


     /** 
      * 渲染项目表单信息
      *  */
     renderForm = () => {
        console.log(this.state.userlist);
        return (
            <div>
            <Form
                {...layout}
                ref={this.formRef}
                className="project-ei-from"
                name="login"
            >
                {/* 日期选择 */}
                <Form.Item
                    style={{ display: 'inline-flex', width: 'calc(45% - 4px)'}}
                    label="选择日期"
                    name="Data"
                    rules={[{ required: false, message: '选择日期' }]}
                >
                    <Space direction="vertical">
                        <DatePicker onChange={this.onChange} key={this.onChange} />
                    </Space>
                </Form.Item>

                {/* 项目下拉框 */}
                <Form.Item
                    style={{ display: 'inline-flex',width: 'calc(55% - 4px)', marginLeft: '8px' }} 
                    label="项目名称"
                    name="projectName"
                    rules={[{ required: false, message: '请输入项目名称' }]}
                >
                    <Select
                        showSearch

                        style={{ width: 200 }}
                        placeholder="请输入项目名称"
                        optionFilterProp="children"
                        filterOption={(input, option) =>
                        option.children.toLowerCase().indexOf(input.toLowerCase()) >= 0
                        }
                    >
                        {
                            // 遍历option
                        this.state.projectName.map((ele,index)=>{
                            return(
                        <option key={ele.projectName}>{ele.projectName}</option>
                            )
                                })
                            }
                        

                    </Select>
                </Form.Item>

                {/* 任务id选择 */}
                <Form.Item
                 style={{ display: 'inline-flex', width: 'calc(45% - 4px)'}}
                    label="任务id"
                    name="taskId"
                    rules={[{ required: false, message: '任务id' }]}
                >
                    <Input type="text" size="10" />
                </Form.Item>
                {/* 用户下拉框选择 */}
                <Form.Item
                style={{ display: 'inline-flex',width: 'calc(55% - 4px)', marginLeft: '8px' }} 
                    label="用户"
                    name="userName"
                    rules={[{ required: false, message: '请输入用户' }]}
                >
                    <Select
                        showSearch
                        style={{ width: 200 }}
                        placeholder="请输入用户"
                        dropdownRender={menu=>(
                            <div>
                                {menu}
                                <Divider style={{margin:'2px 0'}}/>,
                                <div style={{padding:'4px 8px 8px 8px',cursor:'pointer'}}>,
                                    <Checkbox>全选</Checkbox>
                                </div>
                            </div>
                        )}
                    >
                        {
                            // console.log(this.state.userlist);
                            // 遍历option
                            this.state.userlist.map((ele,index)=>{
                                return(
                                    <option key={ele.label}>{ele.label}</option>
                                )
                            })
                        }
                    </Select>
                </Form.Item>

                {/* 设置按钮 */}
                 <div  className="table-btn-search">
                 <button form="login" type="submit" onClick={this.handleSearch}>搜索</button>
                </div>
            </Form>

            </div>
        )
    }


    render() { 
        return ( 
            <div className="manage-data-collect">

                <Row className="project-edit-con"  gutter={20}>
                    <Col className="gutter-row" span={24}>
                        <Box title="已采集数据"
                            render={this.renderForm()}      //搜索框
                            ></Box>
                    </Col>
                    <Col className="gutter-row" span={24}>
                        <Box 
                        render={this.renderPro()}       //搜索详情页
                        ></Box>         
                    </Col>
               </Row>
               {/* 翻页功能 */}
               <Pagination showQuickJumper defaultCurrent={1} total={500} onChange={onChangePage} />
            </div>
         );
    }


    getValue=(event)=>{
        //获取被选中的值
        console.log(event.target.value);
        this.setState({
          //默认值改变
          city:event.target.value
        })
    
      }

    /** 渲染项目 */
    renderPro = () => {
        const { proColumns, proData } = this.state;
        return (
            <Table dataSource={proData} columns={proColumns} pagination={false}/>
        )
    }



    // // /** 获取项目列表 */
    getProjectList = () => {
        apiGetProjectList({
            pageNum: 1,
            pageSize: 5,
            json: { 
                projectName: '知文政策'
             }
        }).then((res)=>{
            console.log("++++++++++++++")
                console.log(res)
                console.log("+++++++++++++++++++")
            this.setState({
                proData: res.content && res.content.map((item, index)=>{
                    return {
                        webName: item.webName, // 网站名称
                        sectionTitle: item.sectionTitle, // 板块名称
                        articleID: item.id, // 文章id
                        articleTitle: item.title, // 文章标题
                        date: item.insertDate, // 采集时间  
                    }
                })
                
            })
            console.log(this.formRef.current)

            this.formRef.current.setFieldsValue({//反映到界面上的
                webName: res.webName, // 网站名称
                sectionTitle: res.sectionTitle, // 板块名称
                articleID: res.id, // 文章id
                articleTitle: res.title, // 文章标题
                date: res.insertDate, // 采集时间           

            })


        })
    }

    // // 渲染搜索按钮
    // renderSearchBtn=()=>{
    //     return(  
    //         <button
    //             className="project-list-search"
    //             onClick={this.handleSearch}
    //         >搜索
    //         </button>
    //     )
        
    // }

    /** 处理搜索 */
    handleSearch = () => {
        this.formRef.current.validateFields().then((values)=>{
            console.log(values)
            console.log(this.state.date)
            apiGetProjectList({//调用该接口所传参数
                name: values.name, // 项目名称
                projectName: values.projectName,//项目名称
                taskId: values.taskId,//任务id
                userName: values.userName,//用户名
                Data: this.state.date,//时间
            }).then(res=>{
                // this.getProjectList();
                    this.setState({
                    proData: res.content && res.content.map((item, index)=>{
                        return {
                            webName: item.webName, // 网站名称
                            sectionTitle: item.sectionTitle, // 板块名称
                            articleID: item.id, // 文章id
                            articleTitle: item.title, // 文章标题
                            date: item.insertDate, // 采集时间  
                        }
                    })
                })
            })
        }).catch(errorInfo => {
            console.log(errorInfo)
        })
    }

  // /** 获取项目列表 */
//   getProjectList = () => {
//     const { pageSize, pageNum, projectName} = this.state;
//     apiGetProjectList({//*参数*/
//         projectName: projectName,
//         pageSize: pageSize,
//         pageNum: pageNum,
//     }).then((res)=>{//需要的数据*/
//         this.setState({
//             proData: res.content && res.content.map((item, index)=>{
//                 return {
//                     webName: item.webName, // 网站名称
//                     sectionTitle: item.sectionTitle, // 板块名称
//                     articleID: item.id, // 文章id
//                     articleTitle: item.title, // 文章标题
//                     date: item.insertDate, // 采集时间  
//                 }
//             })
//         })
//     })
// }


            /** 获取所有用户信息 */
    getAllUser = () => {
        apiGetAllUser().then(res=>{
            this.setState({
                userlist: res && res.map(item=>{
                    return {
                        label: item.userName,
                        value: item.id,
                    }
                })
            })
        })
    }

    /*获取项目名称*/
    getProjectName=()=>{
        apoGetProjectName().then(res=>{
            this.setState({
                projectName: res&&res.map(item=>{
                    return{
                        projectName: item.name,
                    }
                })

            })
        })
    } 


    /** 日期改变事件 */
    onChange = (date, dateString) => {
        this.setState({
            date: dateString
        })
        console.log(date, dateString);
    }

    // 获取单个文章详情
    getArtDetil=()=>{

    }

  

}
  /**页码更改 */
function onChangePage (pageNumber) {
    console.log('Page: ', pageNumber);
  }
 
export default collectedData;