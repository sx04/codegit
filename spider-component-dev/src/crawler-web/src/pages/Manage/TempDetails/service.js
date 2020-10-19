import Model from '../../../api';
import { BASEURL } from '../../../common/config';

/** 获取模板列表 */
export const apiGetTempList = ({projectId}) => {
    return Model.get(
        BASEURL + '/project/field/query',
        {projectId},
        true
    )
}

/** 删除 */
export const apiDeleteFile = ({id}) => {
    return Model.post(
        BASEURL + '/project/field/delete',
        {id},
        true
    )
}

/** 新增字段 */
export const apiAddFile = ({ fileCode, projectId,  fileName,  fileSort, fileType}) => {
    return Model.post(
        BASEURL + '/project/field/add',
        {
            fileCode,
            projectId,
            fileName,
            fileSort,
            fileType
        },
        true,
        true
    )
}