#!/usr/bin/env bash

setup() {
  sudo ip tuntap add mode tap name tun2 user "$USER"
  sudo ip link set up dev tun2
  sudo ip addr add 10.0.0.1/24 dev tun2
}
setup
