/*
 * Copyright (C) 2014 Laptop
 *
 * 
 * This file is a part of pircboty
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

package net.ae97.pokebot.permissions;

import java.util.HashSet;

public class PermissionSet extends HashSet<Permission> {

    public boolean contains(String permission) {
        return false;
    }

    public Permission get(String permission) {
        return null;
    }

}
