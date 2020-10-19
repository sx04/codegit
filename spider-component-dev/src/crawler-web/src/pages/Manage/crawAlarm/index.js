/**
 * 管理员-爬虫报警
 * 创建： 2020-09-17
 */
import './index.scss';
import React, { Component } from 'react';
import { Form, Row, Col,DatePicker, Space, Select} from 'antd';
import Table from '../../../components/Table';
import Box from '../../../components/Box';
import { apiGetcrawAlarm, apoGetProjectName } from './service';
        
const layout = {
    labelCol: { span: 6 },
    wrapperCol: { span: 18 },
};

class crawAlarm extends Component {
    formRef = React.createRef();    
    constructor(props) {
        super(props);
        this.state = {
            /**表头
             * 模板id 列表网络异常 访问失败 列表模板失败 文章网络异常 文章url失败 文章模板失败 是否处理 处理人 处理时间 
             */
            projectName:[],   //项目名称下拉框
            dealStatu: [0,1],
            proColumns: [
                // {
                //     title: '模板id',
                //     dataIndex: 'templateId',
                //     key: 'templateId',
                //     width: 90,
                //     render: (text, record) => (
                //         <div className="project-list-btn">
                //           <button>模板id</button>
                //         </div>
                //     )
                // },
                {
                    title: '模板id',
                    dataIndex: 'templateId',
                    key: 'templateId',
                    width: 90
                },
                {
                    title: '列表网络异常',
                    dataIndex: 'listNetworkCount',
                    key: 'listNetworkCount',
                    width: 150
                },
                {
                    title: '访问失败',
                    dataIndex: 'listUrlCount',
                    key: 'listUrlCount',
                    width: 100
                },
                {
                    title: '列表模板失败',
                    dataIndex: 'listTemplateCount',
                    key: 'listTemplateCount',
                    width: 120
                },
                {
                    title: '文章网络异常',
                    dataIndex: 'docNetworkCount',
                    key: 'docNetworkCount',
                    width: 150
                },
                {
                    title: '文章url失败',
                    dataIndex: 'docUrlCount',
                    key: 'docUrlCount',
                    width: 120
                },
                {
                    title: '文章模板失败',
                    dataIndex: 'docTemplateCount',
                    key: 'docTemplateCount',
                    width: 150
                },
                {
                    title: '是否处理',
                    dataIndex: 'isDeal',
                    key: 'isDeal',
                    width: 90
                },
                {
                    title: '处理人',
                    dataIndex: 'userName',
                    key: 'userName',
                    width: 90
                },
                {
                    title: '处理时间',
                    dataIndex: 'dealTime',
                    key: 'dealTime',
                    width: 100
                },

            ], // 项目表格表头配置项
            proData: [], // 项目列表数据
        }
    }

    componentDidMount(){
        this.getProjectList(); // 获取项目列表
        this.getProjectName();//获取项目名称
        this.handleSearch();

    }

    render() { 
        return ( 
            <div className="manage-project-list">
                 <Row gutter={20}>
                        <Col className="gutter-row" span={24}>
                            <Box title="爬虫报警"
                                // render={this.renderFrom()}
                                 render={this.renderFrom()}
                            ></Box>
                        </Col>

                        <Col className="gutter-row" span={24}>
                            <Box
                            render={this.renderPro()}
                            >
                            </Box>
                        </Col>
                </Row>
            </div>
         );
    }

    /** 渲染项目 */
    renderPro = () => {
        const { proColumns, proData } = this.state;
        return (
            <Table dataSource={proData} columns={proColumns} pagination={false}/>
        )
    }

    /**搜索表单 */
    renderFrom = () =>{
        return(
            <div>
                <Form
                {...layout}
                ref={this.formRef}
                className="project-ei-from"
                name="login"
                >
                    {/* 日期选择 */}
                <Form.Item
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

                {/* 处理状态下拉框 */}
                <Form.Item
                    label="处理状态"
                    name="isDeal"
                    rules={[{ required: false, message: '请选择处理状态' }]}
                >
                    <Select
                        showSearch

                        style={{ width: 200 }}
                        placeholder="请选择处理状态"
                        optionFilterProp="children"
                        filterOption={(input, option) =>
                        option.children.toLowerCase().indexOf(input.toLowerCase()) >= 0
                        }
                    >
                        {
                            // 遍历option
                        this.state.dealStatu.map((ele,index)=>{
                            return(
                                 <option key={ele}>{ele}</option>
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
  
        /** 获取项目列表 */
        getProjectList = () => {
            apiGetcrawAlarm(
                {
                    pageNum:1,
                    pageSize: 5,
                    json: { 
                        projectName: '知文政策'
                     }
                }
            ).then((res)=>{
                console.log("++++++++++++++")
                console.log(res)
                console.log("+++++++++++++++++++")
                    this.setState({
                        proData: res.content && res.content.map((item, index)=>{
                            return {
                                templateId: item.templateId,
                                listNetworkCount: item.listNetworkCount,
                                listUrlCount: item.listUrlCount,
                                listTemplateCount: item.listTemplateCount,
                                docNetworkCount: item.docNetworkCount,
                                docUrlCount: item.docUrlCount,
                                docTemplateCount: item.docTemplateCount,
                                isDeal: item.isDeal,
                                userName: item.userName,
                                dealTime: item.dealTime
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


  

    // 搜索按钮
    handleSearch=()=>{
        this.formRef.current.validateFields().then((values)=>{
            apiGetcrawAlarm({
                taskId: values.taskId,
                userName: values.userName,
                Data: values.Data,
                projectName: values.projectName

            }).then(res=>{

                this.setState({
                    proData: res.content && res.content.map((item, index)=>{
                        return {
                            templateId: item.templateId,
                            listNetworkCount: item.listNetworkCount,
                            listUrlCount: item.listUrlCount,
                            listTemplateCount: item.listTemplateCount,
                            docNetworkCount: item.docNetworkCount,
                            docUrlCount: item.docUrlCount,
                            docTemplateCount: item.docTemplateCount,
                            isDeal: item.isDeal,
                            userName: item.userName,
                            dealTime: item.dealTime
                        }
                    })
                })

            })
        }).catch(errorInfo=>{
            console.log(errorInfo)
        })
    }



}
 
export default crawAlarm;

