import { Select, Spin } from 'antd';
import debounce from 'lodash/debounce';
import React, { Component } from 'react';

// 搜索用户
// 一个带有远程搜索，防抖控制，请求时序控制，加载状态的多选示例。

const { Option } = Select;

class UserRemoteSelect extends React.Component {
  constructor(props) {
    super(props);
    this.lastFetchId = 0;
    this.fetchUser = debounce(this.fetchUser, 800);
  }

  state = {
    data: [],
    value: [],
    fetching: false,
  };

  fetchUser = value => {
    console.log('fetching user', value);
    this.lastFetchId += 1;
    const fetchId = this.lastFetchId;
    this.setState({ data: [], fetching: true });
    fetch('https://randomuser.me/api/?results=5')
      .then(response => response.json())
      .then(body => {
        if (fetchId !== this.lastFetchId) {
          // for fetch callback order
          return;
        }
        const data = body.results.map(user => ({
          text: `${user.name.first} ${user.name.last}`,
          value: user.login.username,
        }));
        this.setState({ data, fetching: false });
      });
  };

  handleChange = value => {
    this.setState({
      value,
      data: [],
      fetching: false,
    });
  };

  render() {
    const { fetching, data, value } = this.state;
    return (
      <Select
        mode="multiple"//设置 Select 的模式为多选或标签
        labelInValue//是否把每个选项的 label 包装到 value 中，会把 Select 的 value 类型从 string 变为 { value: string, label: ReactNode } 的格式
        value={value}//指定当前选中的条目
        placeholder="Select users"//选择框默认文字
        notFoundContent={fetching ? <Spin size="small" /> : null}//当下拉列表为空时显示的内容
        filterOption={false}//是否根据输入项进行筛选
        onSearch={this.fetchUser}//文本框值变化时回调
        onChange={this.handleChange}//选中 option，或 input 的 value 变化时，调用此函数
        style={{ width: '100%' }}
      >
        {data.map(d => (
          <Option key={d.value}>{d.text}</Option>
        ))}
      </Select>
    );
  }
}

ReactDOM.render(<UserRemoteSelect />, mountNode);