package com.qcl.utils;

import com.alibaba.druid.util.StringUtils;
import com.qcl.constants.IndexTypeEnum;
import com.qcl.entity.UserDTO;

public class IndexNameResolver {

    /**
     * 根据用户ID和索引类型生成索引名称
     * @param indexType 索引类型（node, edge, trace等）
     * @return 完整的索引名称
     */
    public static String generate(UserDTO user, Long targetUserId, String indexType) {
        if (user == null){
            throw new IllegalArgumentException("User cannot be null");
        }
        if (user.getUserId() == null) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        if (user.getRole() == null || user.getRole().trim().isEmpty()){
            throw new IllegalArgumentException("User role cannot be null or empty");
        }

        if (indexType == null || indexType.trim().isEmpty()) {
            throw new IllegalArgumentException("Index type cannot be null or empty");
        }

        if (null == IndexTypeEnum.fromCode(indexType)){
            throw new IllegalArgumentException("Invalid index type");
        }

        if (StringUtils.equalsIgnoreCase(user.getRole(), "admin")){
            if (targetUserId == null){
                throw new IllegalArgumentException("Target user ID cannot be null");
            }
            return String.format("%s_%s", indexType.toLowerCase(), targetUserId);
        }else {
            return String.format("%s_%s", indexType.toLowerCase(), user.getUserId());
        }
    }

}
