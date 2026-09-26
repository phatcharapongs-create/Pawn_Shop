package com.kku.pawnshop.domain.state;

import com.kku.pawnshop.domain.enums.TicketStatus;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * แปลง TicketStatus ที่เก็บในฐานข้อมูล ให้เป็นอ็อบเจกต์ TicketState
 *
 * OCP: ไม่มี switch หรือ if-else เลย Spring ฉีด TicketState ทุกตัวที่เป็น
 * @Component เข้ามาใน List แล้วคลาสนี้สร้าง map จาก status() ของแต่ละตัว
 * เพิ่มสถานะใหม่ = เพิ่มคลาสใหม่ 1 ไฟล์ ไม่ต้องแก้ไฟล์นี้เลย
 */
@Component
public class TicketStateFactory {

    private final Map<TicketStatus, TicketState> registry = new EnumMap<>(TicketStatus.class);

    public TicketStateFactory(List<TicketState> states) {
        for (TicketState state : states) {
            registry.put(state.status(), state);
        }
    }

    public TicketState stateOf(TicketStatus status) {
        TicketState state = registry.get(status);
        if (state == null) {
            throw new IllegalStateException("ไม่พบ TicketState สำหรับสถานะ " + status);
        }
        return state;
    }
}
