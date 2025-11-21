package cn.cdut.ws.handler;

import cn.cdut.ws.model.FriendRequest;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * FriendRequest.Status 枚举类型处理器
 * 将数据库中的整数转换为枚举
 */
@MappedTypes(FriendRequest.Status.class)
public class FriendRequestStatusTypeHandler extends BaseTypeHandler<FriendRequest.Status> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, FriendRequest.Status parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getCode());
    }

    @Override
    public FriendRequest.Status getNullableResult(ResultSet rs, String columnName) throws SQLException {
        int code = rs.getInt(columnName);
        return rs.wasNull() ? null : FriendRequest.Status.fromCode(code);
    }

    @Override
    public FriendRequest.Status getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        int code = rs.getInt(columnIndex);
        return rs.wasNull() ? null : FriendRequest.Status.fromCode(code);
    }

    @Override
    public FriendRequest.Status getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        int code = cs.getInt(columnIndex);
        return cs.wasNull() ? null : FriendRequest.Status.fromCode(code);
    }
}
