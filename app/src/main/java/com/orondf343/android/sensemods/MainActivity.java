/*
 * Copyright (C) 2016 OronDF343
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

package com.orondf343.android.sensemods;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.stericson.RootShell.RootShell;
import com.stericson.RootShell.exceptions.RootDeniedException;
import com.stericson.RootShell.execution.Command;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onActivateClick(View view) {
        if (RootShell.isAccessGiven()) {
            Command cmd = new Command(0, "mount -o rw,remount /system",
                    "echo \"#!/system/bin/sh\n\necho 1 > /sys/keyboard/vol_wakeup\nchmod 444 /sys/keyboard/vol_wakeup\" > /system/etc/init.d/vol2wake",
                    "chmod 755 /system/etc/init.d/vol2wake",
                    "mount -o ro,remount /system");
            try {
                RootShell.getShell(true).add(cmd);
                Toast.makeText(this, "Activated", Toast.LENGTH_LONG).show();
                return;
            } catch (IOException | TimeoutException | RootDeniedException e) {
                e.printStackTrace();
            }
        }
        Toast.makeText(this, "Failed to get root access!", Toast.LENGTH_LONG).show();
    }

    public void onDeactivateClick(View view) {
        if (RootShell.isAccessGiven()) {
            Command cmd = new Command(0, "mount -o rw,remount /system",
                    "rm -f /system/etc/init.d/vol2wake",
                    "mount -o ro,remount /system");
            try {
                RootShell.getShell(true).add(cmd).finish();
                Toast.makeText(this, "Deactivated", Toast.LENGTH_LONG).show();
                return;
            } catch (IOException | TimeoutException | RootDeniedException e) {
                e.printStackTrace();
            }
        }
        Toast.makeText(this, "Failed to get root access!", Toast.LENGTH_LONG).show();
    }
}
