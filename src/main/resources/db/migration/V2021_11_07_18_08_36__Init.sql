CREATE TABLE IF NOT EXISTS user_data
(
    chat_id bigserial PRIMARY KEY,
    user_profile_data jsonb
);

GRANT ALL ON TABLE public.user_data TO testuser;