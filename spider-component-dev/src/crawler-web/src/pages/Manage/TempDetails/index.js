/**
 * 管理员-模板详情页
 * 创建： 2020-09-18
 */
import './index.scss';
import React, { Component } from 'react';
import { Row, Col, message } from 'antd';
import Table from '../../../components/Table';
import Box from '../../../components/Box';
import Add from './add';
import { apiGetTempList, apiDeleteFile } from './service';
import { _param } from '../../../common/utils/common';
import _isEqual from "lodash/isEqual";

class TempDetails extends Component {
    constructor(props) {
        super(props);
        this.state = {
            tempColumns: [
                {
                    title: '字段编码',
                    dataIndex: 'code',
                    key: 'code',
                    width: 100
                },
                {
                    title: '名称',
                    dataIndex: 'name',
                    key: 'name',
                    width: 100
                },
                {
                    title: '排序号',
                    dataIndex: 'order',
                    key: 'order',
                    width: 100
                },
                {
                    title: '类型',
                    dataIndex: 'type',
                    key: 'type',
                    width: 100
                },
                {
                    title: '操作',
                    dataIndex: 'other',
                    key: 'other',
                    width: 100,
                    render: (text, record) => (
                        <div className="temp-list-btn">
                          <button onClick={()=>{this.handleDelete(record.id)}}>删除</button>
                        </div>
                    )
                }
            ], // 表格表头配置项
            tempList: [], // 模板列表
            isShowPop: false, // 是否显示新增字段弹窗
        }
    }

    componentDidMount(){
        this.getTempList(); // 获取模板列表
    }

    componentDidUpdate(preProps) {
        if (!_isEqual(this.props, preProps)) {
            this.getTempList();
        }
    }

    render() { 
        const { isShowPop } = this.state;
        return ( 
            <div className="manage-temp-details">
                {
                    isShowPop && 
                    <Add 
                        getTempList={this.getTempList}
                        handleClose={()=>{this.setState({isShowPop: false})}}
                    />
                }
                <span
                    className="temp-details-back"
                    onClick={()=>{this.props.history.push('/manage/proCreate?id='+ _param('projectId'))}}
                >&lt;&lt;返回上一级</span>
                 <Row gutter={20}>
                        <Col className="gutter-row" span={24}>
                            <Box title="模板列表信息"
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
        const { tempColumns, tempList } = this.state;
        return (
            <Table dataSource={tempList} columns={tempColumns} pagination={false}/>
        )
    }

    /** 渲染新增项目按钮 */
    renderMore = () => {
        return(
            <button 
                className="temp-list-more" 
                onClick={()=>{this.setState({isShowPop: true})}}
            >新增字段</button>
        )
    }

    /** 获取模板列表 */
    getTempList = () => {
        apiGetTempList({
            projectId: _param('projectId')
        }).then((res)=>{
            console.log(res)
            this.setState({
                tempList: res && res.map((item, index)=>{
                    return {
                        key: index,
                        id: item.id,
                        code: item.fileCode,
                        name: item.fileName,
                        order: item.fileSort,
                        type: item.fileType
                    }
                })
            })
        })
    }

    /** 处理删除 */
    handleDelete = (id) => {
        apiDeleteFile({id}).then(res=>{
            message.success('删除成功');
            this.getTempList();
        })
    }
}
 
export default TempDetails;