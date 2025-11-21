package cn.cdut.ws.handler;

import cn.cdut.ws.model.Friendship;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Friendship.Status 枚举类型处理器
 * 将数据库中的整数转换为枚举
 */
@MappedTypes(Friendship.Status.class)
public class FriendshipStatusTypeHandler extends BaseTypeHandler<Friendship.Status> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Friendship.Status parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getCode());
    }

    @Override
    public Friendship.Status getNullableResult(ResultSet rs, String columnName) throws SQLException {
        int code = rs.getInt(columnName);
        return rs.wasNull() ? null : Friendship.Status.fromCode(code);
    }

    @Override
    public Friendship.Status getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        int code = rs.getInt(columnIndex);
        return rs.wasNull() ? null : Friendship.Status.fromCode(code);
    }

    @Override
    public Friendship.Status getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        int code = cs.getInt(columnIndex);
        return cs.wasNull() ? null : Friendship.Status.fromCode(code);
    }
}
