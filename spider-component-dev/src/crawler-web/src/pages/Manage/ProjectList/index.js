/**
 * 管理员-项目列表页
 * 创建： 2020-09-17
 */
import './index.scss';
import React, { Component } from 'react';
import { Row, Col, message } from 'antd';
import Table from '../../../components/Table';
import Box from '../../../components/Box';
import { apiGetProjectList, apiChangeProStatus } from './service';

class ProjectList extends Component {
    constructor(props) {
        super(props);
        this.state = {
            proColumns: [
                {
                    title: '项目名称',
                    dataIndex: 'name',
                    key: 'name',
                    width: 100
                },
                {
                    title: '项目英文名',
                    dataIndex: 'englishName',
                    key: 'englishName',
                    width: 100
                },
                {
                    title: '项目调度时间',
                    dataIndex: 'date',
                    key: 'date',
                    width: 100
                },
                {
                    title: '项目优先级',
                    dataIndex: 'priority',
                    key: 'priority',
                    width: 100
                },
                {
                    title: '项目负责人',
                    dataIndex: 'person',
                    key: 'person',
                    width: 100
                },
                {
                    title: '操作',
                    dataIndex: 'other',
                    key: 'other',
                    width: 150,
                    render: (text, record) => (
                        <div className="project-list-btn">
                          <button onClick={()=>{this.handleStartPro(record)}}>{record.isRun ? '启动' : '关闭' }</button>
                          {/*点击修改按钮 转到http://localhost:3000/manage/proCreate?id=zhiwen  */}
                          <button onClick={()=>{this.props.history.push('/manage/proCreate?id=' + record.id)}}>修改</button>
                          <button>任务管理</button>
                        </div>
                    )
                }
            ], // 项目表格表头配置项
            proData: [] // 项目列表
        }
    }

    componentDidMount(){
        this.getProjectList(); // 获取项目列表
    }

    render() { 
        return ( 
            <div className="manage-project-list">
                 <Row gutter={20}>
                        <Col className="gutter-row" span={24}>
                            <Box title="项目信息"
                                extra={this.renderMore('/manage/home')}
                                render={this.renderPro()}
                            ></Box>
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

    /** 渲染新增项目按钮 */
    renderMore = () => {
        return(
            <button 
                className="project-list-more" 
                onClick={()=>{this.props.history.push("/manage/proCreate")}}
            >新增项目</button>
        )
    }

    /** 获取项目列表 */
    getProjectList = () => {
        apiGetProjectList().then((res)=>{
            console.log(res)
            // if(res && res.code === 200){
                this.setState({
                    proData: res && res.map((item, index)=>{
                        return {
                            id: item.code,
                            key: index,
                            name: item.name,
                            englishName: item.code,
                            date: item.dispatchTime,
                            priority: item.priorLevel,
                            person: item.users,
                            isRun: item.isRun
                        }
                    })
                })
            // }else{
            //     message.error(res.msg);
            // }
        })
    }

    /** 启动/关闭项目 */
    handleStartPro = item => {
        apiChangeProStatus({
            code: item.id,
            isRun: item.isRun === 1 ? 0 : 1
        }).then(res=>{
            console.log(res)
            // if(res && res.code === 200){
               this.getProjectList();
            // }else{
            //     message.error(res.msg);
            // }
        })
    }
}
 
export default ProjectList;