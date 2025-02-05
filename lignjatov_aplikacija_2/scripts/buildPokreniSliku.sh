docker stop lignjatov_payara_micro
docker rm lignjatov_payara_micro
docker image rm lignjatov_payara_micro:6.2023.4

./scripts/pripremiSliku.sh
./scripts/pokreniSliku.sh
