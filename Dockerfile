FROM hseeberger/scala-sbt:16.0.1_1.5.4_2.13.6
RUN apt-get update && apt-get install -y libxrender1 libxtst6 libxi6 libgl1-mesa-glx libgtk-3-0
WORKDIR /Catan
ADD . /Catan
RUN rm -r src/main/scala/META-INF
RUN sbt compile
CMD sbt run


#docker build --no-cache -t catan .
#docker run -it --rm -e DISPLAY=host.docker.internal:0.0 catan