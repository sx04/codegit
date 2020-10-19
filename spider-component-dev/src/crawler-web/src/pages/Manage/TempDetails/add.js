/**
 * 新增字段弹窗
 * 创建： 2020-09-18
 */
import "./index.scss";
import "../../../common/styles/pop.scss";
import React, { Component } from "react";
import { Input, Form, Select, message } from "antd";
import { CloseOutlined } from '@ant-design/icons';
import { apiAddFile } from './service';
import { _param } from "../../../common/utils/common";

const layout = {
    labelCol: { span: 6 },
    wrapperCol: { span: 18 },
};
const { Option } = Select;

class Add extends Component {
  constructor(props) {
    super(props);
    this.state = { };
  }

  render() {
    return (
      <div className="temp-add-pop pop-wrap">
        <div className="pop-content">
          <span className="pop-close" onClick={this.props.handleClose}>
            <CloseOutlined />
          </span>
          <h2 className="pop-title">新增字段</h2>
          {this.renderForm()}
        </div>
      </div>
    );
  }

  /** 渲染表单 */
  renderForm = () => {
      return (
        <Form
            {...layout}
            ref={this.formRef}
            className="temp-add-from"
            name="login"
            onFinish={this.handleSave}
        >
            <Form.Item
                label="字段编码"
                name="code"
                rules={[{ required: true, message: '请输入字段编码' }]}
            >
                <Input />
            </Form.Item>
            <Form.Item
                label="名称"
                name="name"
                rules={[{ required: true, message: '请输入名称' }]}
            >
                <Input />
            </Form.Item>
            <Form.Item
                label="排序号"
                name="order"
                rules={[{ required: true, message: '请输入排序号' }]}
            >
                <Input />
            </Form.Item>
            <Form.Item
                label="类型"
                name="type"
                rules={[{ required: true, message: '请输入排序号' }]}
            >
                <Select>
                    <Option value="varchar">varchar</Option>
                    <Option value="int">int</Option>
                    <Option value="text">text</Option>
                </Select>
            </Form.Item>
            <button className="pop-btn">保存</button>
        </Form>
      )
  }

  /** 处理保存 */
  handleSave = (values) => {
    apiAddFile({
        projectId: _param('projectId'),
        fileCode: values.code,
        fileName: values.name,
        fileSort: values.order,
        fileType: values.type,
    }).then(res=>{
        if(res==='SUCCESS'){
            this.props.handleClose();
            this.props.getTempList();
        }else{
            message.error(res);
        }
    })
  }
}

export default Add;
