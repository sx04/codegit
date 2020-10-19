/**
 * 管理员-首页
 * 创建： 2020-09-16
 */
import './index.scss';
import React, { Component } from 'react';
import { Row, Col, DatePicker } from 'antd';
import Table from '../../../components/Table';
import Box from '../../../components/Box';
import Bar from '../../../components/Echarts/Bar';

const { RangePicker } = DatePicker;

class Home extends Component {
    constructor(props) {
        super(props);
        this.state = {
            proColumns: [
                {
                    title: '项目',
                    dataIndex: 'name',
                    key: 'name',
                    },
                {
                    title: '网站',
                    dataIndex: 'http',
                    key: 'http',
                },
                {
                    title: '模板',
                    dataIndex: 'temp',
                    key: 'temp',
                },
                {
                    title: '配置人',
                    dataIndex: 'person',
                    key: 'person',
                },
                {
                    title: '完成时间',
                    dataIndex: 'date',
                    key: 'date',
                }
            ],
            errorColumns: [
                {
                    title: '模板id',
                    dataIndex: 'id',
                    key: 'id',
                },
                {
                    title: '列表网络异常',
                    dataIndex: 'listHttpError',
                    key: 'listHttpError',
                },
                {
                    title: '访问失败',
                    dataIndex: 'fail',
                    key: 'fail',
                },
                {
                    title: '列表模板失败',
                    dataIndex: 'tempError',
                    key: 'tempError',
                },
                {
                    title: '文章网络异常',
                    dataIndex: 'artHttpError',
                    key: 'artHttpError',
                },
                {
                    title: '文章url失败',
                    dataIndex: 'artUrlError',
                    key: 'artUrlError',
                },
                {
                    title: '文章模板失败',
                    dataIndex: 'artTempError',
                    key: 'artTempError',
                },
                {
                    title: '配置人',
                    dataIndex: 'person',
                    key: 'person',
                }
            ],
            errorData: [
                {
                    key: 1,
                    id: 1,
                    listHttpError: 2,
                    fail: 3,
                    tempError: 2,
                    artHttpError: 1,
                    artUrlError: 1,
                    artTempError: 1,
                    person: '张三'
                },
                {
                    key: 2,
                    id: 2,
                    listHttpError: 2,
                    fail: 3,
                    tempError: 2,
                    artHttpError: 1,
                    artUrlError: 1,
                    artTempError: 1,
                    person: '张三'
                },
                {
                    key: 3,
                    id: 3,
                    listHttpError: 2,
                    fail: 3,
                    tempError: 2,
                    artHttpError: 1,
                    artUrlError: 1,
                    artTempError: 1,
                    person: '张三'
                },
                {
                    key: 4,
                    id: 4,
                    listHttpError: 2,
                    fail: 3,
                    tempError: 2,
                    artHttpError: 1,
                    artUrlError: 1,
                    artTempError: 1,
                    person: '张三'
                },
                {
                    key: 5,
                    id: 5,
                    listHttpError: 2,
                    fail: 3,
                    tempError: 2,
                    artHttpError: 1,
                    artUrlError: 1,
                    artTempError: 1,
                    person: '张三'
                },
                {
                    key: 6,
                    id: 6,
                    listHttpError: 2,
                    fail: 3,
                    tempError: 2,
                    artHttpError: 1,
                    artUrlError: 1,
                    artTempError: 1,
                    person: '张三'
                },
                {
                    key: 7,
                    id: 7,
                    listHttpError: 2,
                    fail: 3,
                    tempError: 2,
                    artHttpError: 1,
                    artUrlError: 1,
                    artTempError: 1,
                    person: '张三'
                },
            ],
            proData: [
                {
                  key: '1',
                  name: '胡彦斌',
                  http: 32,
                  temp: '西湖区湖底公园1号',
                  person: '张三',
                  date: '2020-09-09'
                },
                {
                  key: '2',
                  name: '胡彦祖',
                  http: 42,
                  temp: '西湖区湖底公园1号',
                  person: '张三',
                  date: '2020-09-09'
                },
                {
                    key: '3',
                    name: '胡彦斌',
                    http: 32,
                    temp: '西湖区湖底公园1号',
                    person: '张三',
                    date: '2020-09-09'
                },
                {
                    key: '4',
                    name: '胡彦祖',
                    http: 42,
                    temp: '西湖区湖底公园1号',
                    person: '张三',
                    date: '2020-09-09'
                },
                {
                  key: '5',
                  name: '胡彦斌',
                  http: 32,
                  temp: '西湖区湖底公园1号',
                  person: '张三',
                  date: '2020-09-09'
                },
                {
                  key: '6',
                  name: '胡彦祖',
                  http: 42,
                  temp: '西湖区湖底公园1号',
                  person: '张三',
                  date: '2020-09-09'
                },
                {
                    key: '7',
                    name: '胡彦祖',
                    http: 42,
                    temp: '西湖区湖底公园1号',
                    person: '张三',
                    date: '2020-09-09'
                },
            ],
            barData: [
                {
                    label:'1月',
                    value: 2
                },
                {
                    label:'2月',
                    value: 12
                },
                {
                    label:'3月',
                    value: 12
                },

                {
                    label:'4月',
                    value: 22
                },
                {
                    label:'5月',
                    value: 2
                },
                {
                    label:'6月',
                    value: 12
                },
                {
                    label:'7月',
                    value: 12
                },

                {
                    label:'8月',
                    value: 22
                },
            ],
            zhiwen: 'M',
            company: 'M'
        }
    }

    render() { 
        return ( 
            <div className="manage-home">
                 <Row gutter={20}>
                        <Col className="gutter-row" span={12}>
                            <Box title="模板新增"
                                extra={this.renderMore('/manage/home')}
                                render={this.renderPro()}
                            ></Box>
                        </Col>
                        <Col className="gutter-row" span={12}>
                            <Box title="采集异常"
                                extra={this.renderMore('/manage/home')}
                                render={this.renderError()}
                            ></Box>
                        </Col>
                        <Col className="gutter-row" span={24}>
                            <Box title="知文政策-数据采集量统计"
                                extra={this.renderExtra()}
                                render={this.renderCount('zhiwen')}
                            ></Box>
                        </Col>
                        <Col className="gutter-row" span={24}>
                            <Box title="企业信息-数据采集量统计"
                                extra={this.renderExtra()}
                                render={this.renderCount('company')}
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

     /** 渲染异常数据统计 */
     renderError = () => {
        const { errorColumns, errorData } = this.state;
        return (
            <Table dataSource={errorData} columns={errorColumns} pagination={false}/>
        )
    }

    /** 渲染统计数据 */
    renderCount = (id) => {
        const { barData } = this.state;
        return (
            <Row className="home-count">
                <Col className="gutter-row" span={12}>
                    <ul className="home-count-legend f-right">
                        <li 
                            className={this.state[id] === 'M' ?  "home-cl-select" : ""}  
                            onClick={()=>{this.setState({[id]: 'M'})}}
                        >月</li>
                        <li 
                            className={this.state[id] === 'D' ?  "home-cl-select" : ""}  
                            onClick={()=>{this.setState({[id]: 'D'})}}
                        >日</li>
                    </ul>
                   <Bar 
                        barData={barData} 
                        id={id} 
                        height={'300px'}
                        colors={['rgba(0,222,224,.8)', 'rgba(0,186,188,.8)']}
                    />
                </Col>
                <Col className="gutter-row" span={4}>
                  <ul className="home-ci-ul">
                      <li>
                          <span className="home-ciu-name">今日模板新增:</span>
                          <span className="home-ciu-num">15</span>
                      </li>
                      <li>
                          <span className="home-ciu-name">模板总数:</span>
                          <span className="home-ciu-num">15</span>
                      </li>
                      <li>
                          <span className="home-ciu-name">今日数据新增:</span>
                          <span className="home-ciu-num">15</span>
                      </li>
                      <li>
                          <span className="home-ciu-name">数据总数:</span>
                          <span className="home-ciu-num">15</span>
                      </li>
                  </ul>
                </Col>
                <Col className="gutter-row home-ci-search" span={8}>
                    <RangePicker  />
                    <button className="home-cis-btn">查询</button>
                    <ul className="home-cis-ul">
                      <li>
                          <span className="home-ciu-name">模板新增数:</span>
                          <span className="home-ciu-num">15</span>
                      </li>
                      <li>
                          <span className="home-ciu-name">数据新增数:</span>
                          <span className="home-ciu-num">15</span>
                      </li>
                  </ul>
                </Col>
            </Row>
        )
    }

    /** 查看项目详情 */
    renderExtra = () => {
        return(
            <span 
                className="home-more" 
                onClick={()=>{this.props.history.push('/proDetails?id=')}}
            >查看项目详情&gt;&gt;</span>
        )
    }

    /** 渲染查看更多 */
    renderMore = (url) => {
        return(
            <span 
                className="home-more" 
                onClick={()=>{this.props.history.push(url)}}
            >更多&gt;&gt;</span>
        )
    }
}
 
export default Home;