/*
 * Copyright (C) 2022 Moacir da Roza Flores <moacirrf@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.github.moacirrf.netbeans.markdown.ui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 *
 * @author Moacir da Roza Flores <moacirrf@gmail.com>
 */
public abstract class MouseListenerBuilder {

    public static enum EventType {
        CLICKED,
        PRESSED,
        RELEASED,
        ENTERED,
        EXITED
    }

    public static MouseListener build(EventPerformed eventPerformed) {
        return new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                eventPerformed.eventPerformed(e, EventType.CLICKED);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                eventPerformed.eventPerformed(e, EventType.PRESSED);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                eventPerformed.eventPerformed(e, EventType.RELEASED);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                eventPerformed.eventPerformed(e, EventType.ENTERED);

            }

            @Override
            public void mouseExited(MouseEvent e) {
                eventPerformed.eventPerformed(e, EventType.EXITED);
            }
        };
    }

    public interface EventPerformed {

        public void eventPerformed(MouseEvent e, EventType type);
    }
}
