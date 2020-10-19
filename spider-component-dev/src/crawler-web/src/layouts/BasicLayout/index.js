/**
 * 侧边栏菜单框架
 */
import './index.scss';
import React from 'react';
import { Route, Switch } from 'react-router-dom';

import { Row, Col } from 'antd';
import Header from './Header';
import Menu from './Menu';

export default function BasicLayout(props){
    return (
        <div className="basic-layout">
            <Header {...props}/>
            <Row >
                <Col className="basic-layout-left" span={3}>
                    <Menu {...props}/>
                </Col>
                <Col className="basic-layout-right" span={21}>
                    <Switch>
                        {
                            props.children && props.children.map((item, index)=>{
                                return (
                                    <Route {...item} key={index}></Route>
                                )
                            })
                        }
                    </Switch>
                </Col>
            </Row>
        </div>
    );
}