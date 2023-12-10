#!/bin/bash

# Function to delete a deployment
delete_deployment() {
  namespace=$1
  deployment=$2
  kubectl delete deployment $deployment -n $namespace
}

# Main script
namespace="eshop"
deployments=("eshop-inventory" "eshop-billing" "eshop-delivery" "eshop-logistics" "jeager" "eshop-checkout")

echo "Deleting deployments from $namespace"
for deployment in "${deployments[@]}"; do
  delete_deployment $namespace $deployment
done

