package org.csu.ipetstore.mapper;

import org.csu.ipetstore.domain.Sequence;
import org.springframework.stereotype.Repository;

@Repository
public interface SequenceMapper {
    // 得到序列
    Sequence getSequence(Sequence sequence);

    // 更新序列
    boolean updateSequence(Sequence sequence);
}
