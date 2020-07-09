mvn clean install -Dmaven.test.skip=true -f app_1 && \
docker build -t zipkin/app_1 app_1 && \
npm install --prefix app_2 && \
docker build -t zipkin/app_2 app_2 && \
mvn clean install -Dmaven.test.skip=true -f app_3 && \
docker build -t zipkin/app_3 app_3 && \
mvn clean install -Dmaven.test.skip=true -f app_4 && \
docker build -t zipkin/app_4 app_4 && \
npm install --prefix app_5 && \
docker build -t zipkin/app_5 app_5