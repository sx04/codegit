/**
 * 公共组件-表格
 * 创建： 2020-09-16
 */
import './index.scss';
import React, { Component } from 'react';
import { Table } from 'antd';

class _Table extends Component {
    constructor(props) {
        super(props);
        this.state = {}
           
    }

    render() { 
        return (
            <Table {...this.props}/>
        )
    }
}
 
export default _Table;