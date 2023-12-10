#!/bin/bash

# Function to check if a namespace exists
check_namespace() {
  namespace=$1
  kubectl get namespace $namespace &> /dev/null
  return $?
}

# Function to create a namespace
create_namespace() {
  namespace=$1
  kubectl create namespace $namespace
}

# Function to apply deployments to a namespace
apply_deployments() {
  namespace=$1
  for file in "$2"/*.yaml; do
    kubectl apply -f $file -n $namespace
  done
}

# Main script
namespace="eshop"
deployments_dir="./"

if check_namespace $namespace; then
  echo "Namespace $namespace exists"
else
  echo "Namespace $namespace does not exist. Creating..."
  create_namespace $namespace
fi

echo "Applying deployments to $namespace"
apply_deployments $namespace $deployments_dir

