/**
 * 管理员-模板列表页@@@
 * 创建： 2020-09-18
 */
import './index.scss';
import React, { Component } from 'react';
import { Row, Col } from 'antd';
import Table from '../../../components/Table';
import Box from '../../../components/Box';
import { apiGetAllTempList } from './service';
import { _param } from '../../../common/utils/common';

class TempList extends Component {
    constructor(props) {
        super(props);
        this.state = {
            tempAllColumns: [
                {
                    title: '模板编码',
                    dataIndex: 'code',
                    key: 'code',
                    width: 30
                },
                {
                    title: '项目名称',
                    dataIndex: 'name',
                    key: 'name',
                    width: 50
                },
                {
                    title: '网站名称',
                    dataIndex: 'httpName',
                    key: 'httpName',
                    width: 50
                },
                {
                    title: '板块名称',
                    dataIndex: 'partName',
                    key: 'partName',
                    width: 50
                },
                {
                    title: '共用板块',
                    dataIndex: 'commonPart',
                    key: 'commonPart',
                    width: 30
                },
                {
                    title: '模板地址',
                    dataIndex: 'http',
                    key: 'http',
                    width: 100,
                    textWrap: 'word-break',
                    ellipsis: true
                },
                {
                    title: '模板是否正确',
                    dataIndex: 'isCorrect',
                    key: 'isCorrect',
                    width: 60
                },
                {
                    title: '操作',
                    dataIndex: 'other',
                    key: 'other',
                    width: 100,
                    render: (text, record) => (
                        <div className="temp-list-btn">
                          <button onClick={()=>{this.handleEdit(record)}}>编辑</button>
                          <button onClick={()=>{this.handleVerify(record)}}>验证</button>
                          <button onClick={()=>{this.handleStart(record)}}>{record.isRun ? '启动' : '暂停'}</button>
                        </div>
                    )
                }
            ], // 表格表头配置项
            tempAllList: [], // 所有模板列表
            pageSize: 10,
            pageNum: 1,
            total: 0
        }
    }

    componentDidMount(){
        this.getAllTempList(); // 获取所有模板列表
    }

    render() { 
        return ( 
            <div className="manage-temp-list">
                 <Row gutter={20}>
                        <Col className="gutter-row" span={24}>
                            <Box title="模板列表信息"
                                render={this.renderPro()}
                            ></Box>
                        </Col>
                </Row>
            </div>
         );
    }

    /** 渲染项目 */
    renderPro = () => {
        const {  tempAllColumns,  tempAllList, total, pageSize, pageNum } = this.state;
        return (
            <Table 
                dataSource={tempAllList} 
                columns={tempAllColumns} 
                pagination={{
                    total,
                    pageSize,
                    current: pageNum,
                    hideOnSinglePage: true,
                    onChange: page => {
                        this.setState({
                            pageNum: page
                        }, this.getAllTempList)
                    }
                }}
            />
        )
    }

    /** 获取所有模板列表 */
    getAllTempList = () => {
        const { pageSize, pageNum } = this.state;
        apiGetAllTempList({ pageNum, pageSize }).then((res)=>{
            console.log(res)
            const _isCorrect = ['错误', '正确', '未验证']
            this.setState({
                tempAllList: res && res.content && res.content.map((item, index)=>{
                    return {
                        key: index,
                        code: item.id,
                        name: item.name,
                        httpName: item.webName,
                        partName: item.sectionTitle,
                        commonPart: item.groupId,
                        http: item.sectionUrl,
                        isCorrect: _isCorrect[item.isCorrect],
                        isRun: item.isRun
                    }
                }),
                total: parseInt(res.totalElements, Number)
            })
        })
    }

    /** 处理编辑@@@ */
    handleEdit = () => {

    }

    /** 处理验证@@@ */
    handleVerify = () => {

    }

    /** 处理暂停/启动@@@*/
    handleStart = () => {

    }
}
 
export default TempList;