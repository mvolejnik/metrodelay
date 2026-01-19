export VULTR_API_KEY=$(ansible-vault view .vault.yaml --vault-password-file .vault.password | head -2 | grep api_key | awk '{print $2}' | tr -d '"')

cluster=$1
shift

if [ -z "$cluster" ]; then
  >&2 echo missing 'cluster' parameter blue/green
  exit 1
fi

ansible-playbook all.yaml --limit "g_app:&g_${cluster}" -i vultr.yaml --extra-vars "cluster=${cluster}" --extra-vars @.vault.yaml $@
