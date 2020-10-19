/**
 * 路由入口页
 */
import React from 'react';
import { BrowserRouter, Route, Switch } from 'react-router-dom';
import {Provider} from 'mobx-react';
import {ConfigProvider} from 'antd';
import stores from '../store';
import routes from '../route/RouteConfig';
import zhCN from 'antd/es/locale/zh_CN';  // 引入中文包

export default (props) => (
    <Provider {...stores}>
        <ConfigProvider locale={zhCN}>
            <BrowserRouter>
                <Switch>
                    {routes.map((route, i) => {
                        return <Route
                            path={route.path}
                            key={i}
                            render={props => {
                                return <route.component {...props} children={route.children} />
                            }}
                        />
                    })}
                </Switch>
            </BrowserRouter>
        </ConfigProvider>
    </Provider>
);
