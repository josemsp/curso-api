-- Remove the previous unique constraint on email.
ALTER TABLE public.users
DROP CONSTRAINT IF EXISTS uk6dotkott2kjsp8vw4d0m25fb7;

-- Create a unique index for active users only.
-- Email comparison is case-insensitive.
CREATE UNIQUE INDEX uq_users_active_email
ON public.users (LOWER(BTRIM(email)))
WHERE deleted_at IS NULL;