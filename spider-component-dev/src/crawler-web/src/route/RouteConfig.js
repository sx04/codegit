/**
 * 路由配置页
 */
import asyncComponent from "../common/utils/asyncComponent";
import BasicLayout from '../layouts/BasicLayout';

const Login = asyncComponent(() => import("../pages/Login"));
const NotFound = asyncComponent(() => import("../pages/Status/404"));

// 管理员
const ManageHome = asyncComponent(() => import("../pages/Manage/Home"));
const ProjectList = asyncComponent(() => import("../pages/Manage/ProjectList"));
const ProjectEdit = asyncComponent(() => import("../pages/Manage/ProjectEdit"));
const TempDetails = asyncComponent(() => import("../pages/Manage/TempDetails"));
const TempList = asyncComponent(() => import("../pages/Manage/TempList"));
const collectedData = asyncComponent(() => import("../pages/Manage/collectedData"));
const crawAlarm = asyncComponent(() => import("../pages/Manage/crawAlarm"));

// 普通用户
const UserHome = asyncComponent(() => import("../pages/User/Home"));

const routes =  [
    {
        path: "/login",
        exact: true,
        component: Login
    }, 
    {
        path: "/manage",
        component: BasicLayout,
        children: [
            {
                path: "/manage/home",
                exact: true,
                component: ManageHome
            },
            {
                path: "/manage/proList",
                exact: true,
                component: ProjectList
            },
            {
                path: "/manage/proCreate",
                exact: true,
                component: ProjectEdit
            },
            {
                path: "/manage/tempDetails",
                exact: true,
                component: TempDetails
            },
            {
                path: "/manage/tempList",
                exact: true,
                component: TempList
            },
            {
                path: "/manage/collectedData",
                exact: true,
                component: collectedData
            },
            {
                path: "/manage/crawAlarm",
                exact: true,
                component: crawAlarm
            },
            {
                path: "*",
                exact: true,
                component: NotFound
            }
        ],
    },  
    {
        path: "/user",
        component: BasicLayout,
        children: [
            {
                path: "/user/home",
                exact: true,
                component: UserHome
            },
            {
                path: "*",
                exact: true,
                component: NotFound
            }
        ],
    }, 
    {
        path: "/",
        exact: true,
        component: Login
    }, 
    {
        path: "*",
        component: NotFound
    }
];

export default routes