import Model from '../../../api';
import { BASEURL } from '../../../common/config';

/** 获取所有人员信息 */
export const apiGetAllUser = () => {
    return Model.get(
        BASEURL + '/user/query/all',
        {},
        true
    )
}

/** 获取项目详情 */
export const apiGetProInfo = ({code}) => {
    return Model.get(
        BASEURL + '/project/one',
        {code},
        true
    )
}


/** 修改项目基础信息 */
export const apiEditProInfo = ({name, code, priorLevel, dispatchTime, isCanStart, isTable, sysUsers}) => {
    return Model.post(
        BASEURL + '/project/add',
        {
            projectInfoEntity: {
                name,
                code,
                priorLevel,
                dispatchTime,
                isCanStart,
                isTable,
                isValid: 1
            },
            sysUsers
        },
        true,
        true
    )
}