package nodes;

public enum NodeType {
    PROGRAM,
    STMT_LIST,
    STMT,
    FOR_STMT,
    ASSIGN_STMT, //SUBSTと同じ 代入文
    BLOCK,
    IF_BLOCK, //if_prefix? else_blockelse_if_block
    LOOP_BLOCK,
    COND,
    EXPR_LIST,
    EXPR,
    FUNCTION_CALL, //この下？
    STRING_CONSTANT,
    INT_CONSTANT,
    DOUBLE_CONSTANT,
    BOOL_CONSTANT,
    VARIABLE,
    END,
}
