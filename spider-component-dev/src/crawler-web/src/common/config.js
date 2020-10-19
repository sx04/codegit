
// 请求地址前缀
export const BASEURL = '/api';

// 角色
export const ADMINROLE = 'admin'

// 管理员侧边栏菜单
export const ADMIN_SIDEMENU = [
    {
        name: '首页',
        key: 'home',
        route: '/home',
        children: []
    },
    {
        name: '项目创建/编辑',
        key: 'proCreate',
        route: '/proCreate',
        children: []
    },
    {
        name: '项目信息',
        key: 'proList',
        route: '/proList',
        children: []
    },
    {
        name: '项目任务',
        key: 'proTask',
        route: '/proTask',
        children: []
    },
    {
        name: '模板列表信息',
        key: 'tempList',
        route: '/tempList',
        children: []
    },
    {
        name: '爬虫报警',
        key: 'crawAlarm',
        route: '/crawAlarm',
        children: []
    },
    {
        name: '已采集数据',
        key: 'collectedData',
        route: '/collectedData',
        children: []
    },
    {
        name: '审计日志',
        key: 'auditLog',
        route: '/auditLog',
        children: []
    },
    {
        name: '工作量查询',
        key: 'workload',
        route: '/workload',
        children: []
    },
    {
        name: '任务统计',
        key: 'taskStatistics',
        route: '/taskStatistics',
        children: []
    },
    {
        name: '用户管理',
        key: 'userManage',
        route: '/userManage',
        children: []
    },
];

// 普通用户侧边栏菜单
export const USER_SIDEMENU = [
    {
        name: '首页',
        key: 'home',
        route: '/home',
        children: []
    },
    {
        name: '项目任务',
        key: 'proTask',
        route: '/proTask',
        children: []
    },
    {
        name: '模板列表信息',
        key: 'tempInfo',
        route: '/tempInfo',
        children: []
    },
    {
        name: '程序接入',
        key: 'program',
        route: '/program',
        children: []
    },
    {
        name: '爬虫报警',
        key: 'crawAlarm',
        route: '/crawAlarm',
        children: []
    },
    {
        name: '爬虫数据获取列表',
        key: 'dataList',
        route: '/dataList',
        children: []
    }
];
