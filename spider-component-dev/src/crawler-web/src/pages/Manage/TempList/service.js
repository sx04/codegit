import Model from '../../../api';
import { BASEURL } from '../../../common/config';

/** 获取所有模板列表 */
export const apiGetAllTempList = ({pageSize, pageNum}) => {
    return Model.get(
        BASEURL + '/template/query',
        {
            pageSize,
            pageNum
        },
        true
    )
}