package com.enigwed.repository;

import com.enigwed.constant.ESubscriptionLength;
import com.enigwed.entity.SubscriptionPacket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubscriptionPacketRepository extends JpaRepository<SubscriptionPacket, String> {
    Optional<SubscriptionPacket> findByIdAndDeletedAtIsNull(String id);
    Optional<SubscriptionPacket> findBySubscriptionLengthAndDeletedAtIsNull(ESubscriptionLength length);
    List<SubscriptionPacket> findByDeletedAtIsNull();
}
