# 

## Model
www.msaez.io/#/59328372/storming/93e55621aef16e5cfc7076f172bcdb3as

## Before Running Services
### Make sure there is a Kafka server running
```
cd kafka
docker-compose up
```
- Check the Kafka messages:
```
cd infra
docker-compose exec -it kafka /bin/bash
cd /bin
./kafka-console-consumer --bootstrap-server localhost:9092 --topic
```

## Run the backend micro-services
See the README.md files inside the each microservices directory:

- authormanage
- writemanage
- ai
- point
- libraryplatform
- subscribemanage
- outside


## Run API Gateway (Spring Gateway)
```
cd gateway
mvn spring-boot:run
```

## Test by API
- authormanage
```
 http :8088/authors authorId="authorId"name="name"loginId="loginId"password="password"isApproved="isApproved"portfolioUrl="portfolioUrl"
```
- writemanage
```
 http :8088/writings bookId="bookId"title="title"context="context"authorId="authorId"registration="registration"
```
- ai
```
 http :8088/coverDesigns id="id"updatedAt="updatedAt"title="title"imageUrl="imageUrl"generatedBy="generatedBy"createdAt="createdAt"bookId="bookId"
 http :8088/contentAnalyzers id="id"bookId="bookId"language="Language"maxLength="maxLength"classificationType="classificationType"requestedBy="requestedBy"
```
- point
```
 http :8088/points pointId="pointId"userId="userId"pointBalance="pointBalance"standardSignupPoint="standardSignupPoint"ktSignupPoint="ktSignupPoint"amount="amount"usedAt="usedAt"
```
- libraryplatform
```
 http :8088/libraryInfos bookId="bookId"bookTitle="bookTitle"bestseller="bestseller"author="author"selectCount="selectCount"publishDate="publishDate"summary="summary"classficationTpe="classficationTpe"bookimage="bookimage"
```
- subscribemanage
```
 http :8088/subscribers id="id"name="name"isMonthlySubscribed="isMonthlySubscribed"isKt="isKt"
 http :8088/subscribedBooks subscribedBookId="subscribedBookId"status="status"bookId="bookId"
```
- outside
```
```


## Run the frontend
```
cd frontend
npm i
npm run serve
```

## Test by UI
Open a browser to localhost:8088

## Required Utilities

- httpie (alternative for curl / POSTMAN) and network utils
```
sudo apt-get update
sudo apt-get install net-tools
sudo apt install iputils-ping
pip install httpie
```

- kubernetes utilities (kubectl)
```
curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
sudo install -o root -g root -m 0755 kubectl /usr/local/bin/kubectl
```

- aws cli (aws)
```
curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
unzip awscliv2.zip
sudo ./aws/install
```

- eksctl 
```
curl --silent --location "https://github.com/weaveworks/eksctl/releases/latest/download/eksctl_$(uname -s)_amd64.tar.gz" | tar xz -C /tmp
sudo mv /tmp/eksctl /usr/local/bin
```
