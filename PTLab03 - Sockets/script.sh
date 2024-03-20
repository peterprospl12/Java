#!/bin/bash

terminal_width=80
terminal_height=24
spacing=300

gnome-terminal --geometry=${terminal_width}x${terminal_height}+0+1200 -- bash -c "java -cp target/classes org.example.Server; exec bash"

sleep 1

x_pos=0
y_pos=0
terminal_width=40

for i in {0..4}
do
    gnome-terminal --geometry=${terminal_width}x${terminal_height}+${x_pos}+${y_pos} -- bash -c "java -cp target/classes org.example.Client; exec bash" &
    x_pos=$((x_pos + terminal_width + spacing))
done

