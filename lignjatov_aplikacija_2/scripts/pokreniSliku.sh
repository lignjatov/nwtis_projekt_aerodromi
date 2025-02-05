#!/bin/bash
NETWORK=lignjatov_mreza_1

docker run -it -d \
  -p 8070:8080 \
  --network=$NETWORK \
  --ip 200.20.0.4 \
  --name=lignjatov_payara_micro \
  --hostname=lignjatov_payara_micro \
  lignjatov_payara_micro:6.2023.4 \
  --deploy /opt/payara/deployments/lignjatov_aplikacija_2-1.0.0.war \
  --contextroot lignjatov_aplikacija_2 \
  --noCluster &

wait
