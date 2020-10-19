/**
 * 管理员-项目编辑页
 * 创建： 2020-09-17
 */
import './index.scss';
import React, { Component } from 'react';
import { Row, Col, message, Form, Input, Radio, Checkbox, Table } from 'antd';
import NoData from '../../../components/NoData';
import Box from '../../../components/Box';
import InfoBox from '../../../components/InfoBox';
import { _param } from '../../../common/utils/common';
import { apiGetAllUser, apiGetProInfo, apiEditProInfo } from './service';

const layout = {
    labelCol: { span: 6 },
    wrapperCol: { span: 18 },
};
const CheckboxGroup = Checkbox.Group;

class ProjectEdit extends Component {
    formRef = React.createRef();
    constructor(props) {
        super(props);
        this.state = {
            projectId: '', // 项目id

            proDetailsList: [], // 项目详情列表
            proColumns: [
                {
                    title: 'code',
                    dataIndex: 'code',
                    key: 'code',
                    width: 100
                },
                {
                    title: '名称',
                    dataIndex: 'name',
                    key: 'name',
                    width: 100
                }
            ], // 项目表格表头配置项

            personList: [], // 人员列表
            checkedList: [], // 分配的项目人员
            indeterminate: false,
            isPersonCheckAll: false, // 是否全选
        }
    }


    componentDidMount(){
        const id = _param('id');
        if(id){
            this.setState({
                projectId: id
            },this.getProDetails) // 获取项目详情
        }

        this.getAllUser(); // 获取所有用户信息
    }

    render() { 
        const { projectId} = this.state; 
        return ( 
            <div className="manage-project-edit">
                 <Row gutter={20}>
                        <Col className="gutter-row" span={24}>
                            <Box title={projectId ? "项目编辑" : "新增项目"}
                                render={this.renderContent()}
                            ></Box>
                        </Col>
                </Row>
            </div>
         );
    }

    /** 渲染项目编辑页 */
    renderContent = () => {
        const { projectId } = this.state;
        return (
            <Row className="project-edit-con"  gutter={20}>
                <Col className="gutter-row" span={24}>
                    <InfoBox title="项目基础信息配置"
                        extra={this.renderSaveBtn()}/** 渲染保存按钮 */
                        render={this.renderBaseInfo()}/** 渲染项目基础信息 */
                    ></InfoBox>
                </Col>
                {
                    projectId &&
                        <Col className="gutter-row" span={24}>
                            <InfoBox title="模板详情信息"
                                extra={this.renderEditBtn()}/** 渲染编辑按钮 */
                                render={this.renderDetails()}/** 渲染模板详情信息 */
                            ></InfoBox>
                        </Col>
                }
            </Row>
        )
    }

    /** 渲染保存按钮 */
    renderSaveBtn = () => {
        const { projectId } = this.state;
        return(
            <button 
                className="project-list-more" 
                onClick={this.handleFormSave}
            >
                { projectId ? '修改' : '创建'}
            </button>
        )
    }

    /** 渲染编辑按钮 */
    renderEditBtn = () => {
        const { projectId } = this.state;
        return(
            <button 
                className="project-list-more" 
                onClick={()=>{this.props.history.push("/manage/tempDetails?projectId="+projectId)}}
            >
                
                { projectId ? '修改模板详情' : '添加模板详情'}
            </button>
        )
    }

    /** 渲染项目基础信息 */
    renderBaseInfo = () => {
        return (
            <Row className="project-edit-con"  gutter={20}>
                <Col className="gutter-row" span={12}>
                    {this.renderForm()}
                </Col>
                <Col className="gutter-row" span={12}>
                    {this.renderCheckbox()}
                </Col>
            </Row>
        )
    }

    /** 渲染项目表单信息 */
    renderForm = () => {
        return (
            <Form
                {...layout}
                ref={this.formRef}
                className="project-ei-from"
                name="login"
            >
                <Form.Item
                    label="项目名称"
                    name="name"
                    rules={[{ required: true, message: '请输入项目名称' }]}
                >
                    <Input disabled={_param('id')}/>
                </Form.Item>
                <Form.Item
                    label="项目英文名"
                    name="englishName"
                    rules={[{ required: true, message: '请输入项目英文名' }]}
                >
                    <Input  disabled={_param('id')}/>
                </Form.Item>
                <Form.Item
                    label="项目优先级"
                    name="priority"
                    rules={[{ required: true, message: '请输入项目优先级' }]}
                >
                    <Input />
                </Form.Item>
                <Form.Item
                    label="项目调度时间"
                    name="date"
                    rules={[{ required: true, message: '请输入项目调度时间' }]}
                >
                    <Input />
                </Form.Item>
                <Form.Item
                    label="任务启动权限"
                    name="isCanstart"
                    rules={[{ required: true, message: '请选择' }]}
                >
                <Radio.Group onChange={this.onChange}>
                        <Radio value={1}>有</Radio>
                        <Radio value={0}>没有</Radio>
                    </Radio.Group>
                </Form.Item>
                <Form.Item
                    label="是否为表格"
                    name="isTable"
                    rules={[{ required: true, message: '请选择' }]}
                >
                <Radio.Group onChange={this.onChange}>
                        <Radio value={1}>是</Radio>
                        <Radio value={0}>否</Radio>
                    </Radio.Group>
                </Form.Item>
            </Form>
        )
    }

    /** 渲染项目人员分配 */
    renderCheckbox = () => {
        const { personList, checkedList, isPersonCheckAll, indeterminate } = this.state;
        return (
            <div className="project-edit-checkbox">
                <p className="project-ec-name">项目人员分配：</p>
                <Checkbox
                    indeterminate={indeterminate}
                    onChange={this.onPersonCheckAll}
                    checked={isPersonCheckAll}
                >
                    全选
                </Checkbox>
                <CheckboxGroup
                    className="project-ec-group"
                    options={personList}
                    value={checkedList&&checkedList.map(item=>{return item.id})}
                    onChange={this.onPersonChange}
                />
            </div>
        )
    }

    /** 渲染模板详情信息 */
    renderDetails = () => {
        const { proColumns, proDetailsList } = this.state;
        const middleNum = Math.ceil(proDetailsList.length/2);
        if(!(proDetailsList&&proDetailsList.length)){
            return (<NoData />)
        }

        if(proDetailsList && proDetailsList.length === 1){
            return (
                <Table dataSource={proDetailsList} columns={proColumns} pagination={false}/>
            )
        }
        return (
            <Row className="project-edit-details"  gutter={20}>
                <Col className="gutter-row" span={12}>
                    <Table dataSource={proDetailsList.slice(0, middleNum)} columns={proColumns} pagination={false}/>
                </Col>
                <Col className="gutter-row" span={12}>
                    <Table dataSource={proDetailsList.slice(middleNum, proDetailsList.length)} columns={proColumns} pagination={false}/>
                </Col>
            </Row>
        )
    }

    /** 分配人员选中某一个人事件 */
    onPersonChange = _checkedList => {
        const { personList } = this.state;
        const checkedList = [];
        personList && personList.map(item=>{
            if(_checkedList.indexOf(item.value)>-1){
                checkedList.push({
                    id: item.value,
                    userName: item.label
                })
            }
        })
        console.log(checkedList)
        this.setState({
          checkedList,
          indeterminate: !!_checkedList.length && _checkedList.length < personList.length,
          isPersonCheckAll: _checkedList.length === personList.length,
        });
    };
    
    /** 分配人员全选事件 */
    onPersonCheckAll = e => {
        const { personList, indeterminate } = this.state;
        this.setState({
            checkedList: e.target.checked ? personList : [],
            indeterminate: !indeterminate,
            isPersonCheckAll: e.target.checked,
        });
    };

    /** 获取项目详情 */
    getProDetails = () => {
        const { projectId } = this.state;
        apiGetProInfo({/** 获取项目详情 */
            code: projectId
        }).then(res=>{
            const info = res.projectInfoEntity;
            this.setState({
                checkedList: res&&res.sysUsers&&res.sysUsers.map(item=>{
                    return {
                        id: item.id,
                        name: item.userName
                    }
                }), // 项目人员分配
                proDetailsList: res && res.projectFileEntities && res.projectFileEntities.map((item, index) => {
                    return {
                        key: index,
                        code: item.fileCode,
                        name: item.fileName
                    }
                }) // 模板详情信息
            })
            console.log(this.formRef.current)

            this.formRef.current.setFieldsValue({
                name: info.name,
                englishName: info.code,
                date: info.dispatchTime,
                priority: info.priorLevel,
                isTable: info.isTable, // 是否为表格
                isCanstart: info.isCanStart, // 是否有启动权限
            })
        })
    }

    /** 获取所有用户信息 */
    getAllUser = () => {
        apiGetAllUser().then(res=>{
            this.setState({
                personList: res && res.map(item=>{
                    return {
                        label: item.userName,
                        value: item.id,
                    }
                })
            })
        })
    }

    /** 处理表单保存 */
    handleFormSave = () => {
        const { checkedList } = this.state;
        console.log(checkedList)
        this.formRef.current.validateFields().then((values)=>{
            apiEditProInfo({/** 修改项目基础信息  以下为调用接口所传参数 */
                name: values.name, // 项目名称
                code: values.englishName, // 项目英文名
                priorLevel: values.priority, // 项目优先级
                dispatchTime: values.date, // 项目调度时间
                isCanStart: values.isCanstart, // 任务启动权限
                isTable: values.isTable, // 是否为表格
                sysUsers: checkedList // 项目人员分配
            }).then(res=>{
                if(_param('id')){//id存在______    /** 获取项目详情 */
                    this.getProDetails();
                }else{
                    this.props.history.push('/manage/proCreate?id='+values.englishName);
                    window.location.reload();
                }
            })
        }).catch(errorInfo => {
            console.log(errorInfo)
        })
    }
}
 
export default ProjectEdit;