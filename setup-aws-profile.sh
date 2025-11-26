#!/bin/bash

# Script to configure AWS profile for finance-app

echo "Setting up AWS profile: finance-app"
echo ""
echo "You'll need:"
echo "  - AWS Access Key ID"
echo "  - AWS Secret Access Key"
echo ""
echo "If you don't have these, create them in AWS Console:"
echo "  https://console.aws.amazon.com/iam/home#/security_credentials"
echo ""

# Configure the profile
aws configure --profile mcp-profile

echo ""
echo "Profile 'mcp-profile' has been configured!"
echo ""
echo "To verify, run:"
echo "  aws sts get-caller-identity --profile finance-app"
echo ""
echo "To use this profile, set:"
echo "  export AWS_PROFILE=finance-app"
echo ""
echo "Or update your MCP configuration to use:"
echo "  "AWS_PROFILE": "mcp-profile"

