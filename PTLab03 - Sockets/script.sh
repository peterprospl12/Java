#!/bin/bash
for i in {1..4}
do
	gnome-terminal -- bash -c "java -cp target/classes org.example.Client; exec bash"
done
